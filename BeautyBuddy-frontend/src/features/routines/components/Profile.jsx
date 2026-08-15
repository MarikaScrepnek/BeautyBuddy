import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { fetchUserActivities } from '../../feed/api/feedApi';
import { searchUsers } from '../../user/api/userApi';
import { editProfile } from '../../auth/api/authApi';
import '../../feed/Feed.css';
import './Profile.css';

function parseActivityPayload(payload) {
    if (payload && typeof payload === 'object') {
        return payload;
    }

    if (typeof payload === 'string') {
        try {
            return JSON.parse(payload);
        } catch {
            return { message: payload };
        }
    }

    return {};
}

function normalizeFeedItem(item) {
    return item?.body ?? item ?? {};
}

function formatProfileValue(value) {
    if (value === null || value === undefined || value === '') {
        return 'N/A';
    }

    if (Array.isArray(value)) {
        const formattedValues = value
            .map((item) => formatProfileValue(item))
            .filter((item) => item !== 'N/A');

        return formattedValues.length > 0 ? formattedValues.join(', ') : 'N/A';
    }

    const text = String(value).trim();

    const friendlyValues = {
        SHE_HER: 'She/Her',
        HE_HIM: 'He/Him',
        THEY_THEM: 'They/Them',
        OTHER: 'Other',
        USA: 'United States',
        CANADA: 'Canada',
        UK: 'United Kingdom',
        AUSTRALIA: 'Australia',
        OILY: 'Oily',
        DRY: 'Dry',
        COMBINATION: 'Combination',
        NORMAL: 'Normal',
        SENSITIVE: 'Sensitive',
        ACNE_PRONE: 'Acne Prone',
        STRAIGHT: 'Straight',
        WAVY: 'Wavy',
        CURLY: 'Curly',
        COILY: 'Coily',
        THIN: 'Thin',
        MEDIUM: 'Medium',
        THICK: 'Thick',
    };

    if (friendlyValues[text.toUpperCase()]) {
        return friendlyValues[text.toUpperCase()];
    }

    return text
        .toLowerCase()
        .replace(/[_/]+/g, ' ')
        .replace(/\s+/g, ' ')
        .trim()
        .split(' ')
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}

function getAddProfileLabel(label) {
    return `Add ${label}`;
}

function normalizeSkinConcernSelections(value) {
    if (Array.isArray(value)) {
        return value.filter(Boolean);
    }

    if (typeof value === 'string') {
        return value
            .split(',')
            .map((item) => item.trim())
            .filter(Boolean);
    }

    return [];
}

function getStoredSkinConcernSelections(username) {
    if (typeof window === 'undefined' || !username) {
        return [];
    }

    try {
        const storedValue = window.localStorage.getItem(`beautybuddy:skin-concerns:${username}`);
        if (!storedValue) {
            return [];
        }

        return normalizeSkinConcernSelections(JSON.parse(storedValue));
    } catch {
        return [];
    }
}

function saveStoredSkinConcernSelections(username, selections) {
    if (typeof window === 'undefined' || !username) {
        return;
    }

    try {
        const normalizedSelections = normalizeSkinConcernSelections(selections);
        if (normalizedSelections.length > 0) {
            window.localStorage.setItem(`beautybuddy:skin-concerns:${username}`, JSON.stringify(normalizedSelections));
        } else {
            window.localStorage.removeItem(`beautybuddy:skin-concerns:${username}`);
        }
    } catch {
        // Ignore storage failures and fall back to the server value.
    }
}

const SKIN_CONCERN_OPTIONS = [
    { value: 'NORMAL', label: 'Normal' },
    { value: 'SENSITIVE', label: 'Sensitive' },
    { value: 'ACNE_PRONE', label: 'Acne Prone' },
];

const MONTHS = [
    { value: '01', label: 'January' },
    { value: '02', label: 'February' },
    { value: '03', label: 'March' },
    { value: '04', label: 'April' },
    { value: '05', label: 'May' },
    { value: '06', label: 'June' },
    { value: '07', label: 'July' },
    { value: '08', label: 'August' },
    { value: '09', label: 'September' },
    { value: '10', label: 'October' },
    { value: '11', label: 'November' },
    { value: '12', label: 'December' },
];

const DAYS = Array.from({ length: 31 }, (_, index) => {
    const day = String(index + 1).padStart(2, '0');
    return { value: day, label: String(index + 1) };
});

const CURRENT_YEAR = new Date().getFullYear();
const YEARS = Array.from({ length: 120 }, (_, index) => {
    const year = String(CURRENT_YEAR - index);
    return { value: year, label: year };
});

function parseBirthdayParts(birthday) {
    if (!birthday) {
        return { month: '', day: '', year: '' };
    }

    const match = String(birthday).match(/^(\d{4})-(\d{2})-(\d{2})/);
    if (match) {
        return { year: match[1], month: match[2], day: match[3] };
    }

    const date = new Date(birthday);
    if (!Number.isNaN(date.getTime())) {
        return {
            month: String(date.getMonth() + 1).padStart(2, '0'),
            day: String(date.getDate()).padStart(2, '0'),
            year: String(date.getFullYear()),
        };
    }

    return { month: '', day: '', year: '' };
}

function buildBirthdayValue({ birthdayMonth, birthdayDay, birthdayYear }) {
    if (!birthdayMonth || !birthdayDay || !birthdayYear) {
        return '';
    }

    return `${birthdayYear}-${birthdayMonth}-${birthdayDay}`;
}

export default function Profile({ username, isOwner }) {
    const [activities, setActivities] = useState([]);
    const navigate = useNavigate();
    const [profile, setProfile] = useState(null);
    const [isEditingProfile, setIsEditingProfile] = useState(false);
    const [editDraft, setEditDraft] = useState({
        pronouns: '',
        birthday: '',
        birthdayMonth: '',
        birthdayDay: '',
        birthdayYear: '',
        country: '',
        skintype: '',
        skincondition: [],
        hairtype: '',
        hairdensity: '',
    });

    useEffect(() => {
        if (!username) {
            setActivities([]);
            setProfile(null);
            return;
        }

        handleFetchUserActivities(username);
        handleFetchProfile(username);
    }, [username]);

    const handleFetchUserActivities = async (profileUsername) => {
        try {
            const data = await fetchUserActivities(profileUsername);
            setActivities(Array.isArray(data?.content) ? data.content.map(normalizeFeedItem) : []);
        } catch (error) {
            console.error('Error fetching user activities:', error);
        }
    };

    const handleFetchProfile = async (profileUsername) => {
        try {
            const users = await searchUsers(profileUsername);
            const currentUser = users.find((entry) => entry.username === profileUsername);
            const normalizedProfile = currentUser
                ? {
                    ...currentUser,
                    pronoun: currentUser.pronoun ?? currentUser.pronouns ?? '',
                    birthday: currentUser.birthday ?? currentUser.birthdate ?? currentUser.dateOfBirth ?? '',
                    country: currentUser.country ?? '',
                    skinType: currentUser.skinType ?? currentUser.skin_type ?? '',
                    skinCondition: currentUser.skinCondition ?? currentUser.skinConditions ?? currentUser.skinConcerns ?? '',
                    hairTexture: currentUser.hairTexture ?? currentUser.hair_texture ?? '',
                    hairDensity: currentUser.hairDensity ?? currentUser.hair_density ?? '',
                }
                : null;
            setProfile(normalizedProfile);
        } catch (error) {
            console.error('Error fetching profile:', error);
            setProfile(null);
        }
    };

    function startEditingProfile() {
        const birthdayParts = parseBirthdayParts(profile?.birthday);
        const skinConcernSelections = getStoredSkinConcernSelections(username);

        setEditDraft({
            pronouns: profile?.pronoun ?? '',
            birthday: profile?.birthday ?? '',
            birthdayMonth: birthdayParts.month,
            birthdayDay: birthdayParts.day,
            birthdayYear: birthdayParts.year,
            country: profile?.country ?? '',
            skintype: profile?.skinType ?? '',
            skincondition: skinConcernSelections.length > 0 ? skinConcernSelections : normalizeSkinConcernSelections(profile?.skinCondition),
            hairtype: profile?.hairTexture ?? '',
            hairdensity: profile?.hairDensity ?? '',
        });
        setIsEditingProfile(true);
    }

    async function handleEditProfile() {
        try {
            const skincondition = normalizeSkinConcernSelections(editDraft.skincondition);
            await editProfile({
                ...editDraft,
                skincondition: skincondition[0] ?? '',
                birthday: buildBirthdayValue(editDraft),
            });
            saveStoredSkinConcernSelections(username, skincondition);
            await handleFetchProfile(username);
            setIsEditingProfile(false);
        } catch (err) {
            console.error('Error updating profile:', err);
        }
      }

    function getDraftFieldValue(label) {
        if (label === 'Pronouns') return editDraft.pronouns;
        if (label === 'Birthday') return editDraft.birthday;
        if (label === 'Country') return editDraft.country;
        if (label === 'Skin Type') return editDraft.skintype;
        if (label === 'Skin Concerns') return editDraft.skincondition;
        if (label === 'Hair Texture') return editDraft.hairtype;
        if (label === 'Hair Density') return editDraft.hairdensity;
        return '';
    }

    function renderProfileValue(label, value) {
        const draftValue = getDraftFieldValue(label);
        const displayValue = isEditingProfile ? draftValue : value;
        return formatProfileValue(displayValue);
    }

    function hasProfileValue(value) {
        if (Array.isArray(value)) {
            return value.length > 0;
        }

        return value !== null && value !== undefined && String(value).trim() !== '';
    }

    function setDraftField(label, value) {
        setEditDraft((current) => ({
            ...current,
            ...(label === 'Pronouns' ? { pronouns: value } : {}),
            ...(label === 'Birthday' ? { birthday: value } : {}),
            ...(label === 'Country' ? { country: value } : {}),
            ...(label === 'Skin Type' ? { skintype: value } : {}),
            ...(label === 'Skin Concerns' ? { skincondition: value } : {}),
            ...(label === 'Hair Texture' ? { hairtype: value } : {}),
            ...(label === 'Hair Density' ? { hairdensity: value } : {}),
        }));
    }

    const skinConcernSelections = getStoredSkinConcernSelections(username);

    const profileSections = [
        {
            key: 'general',
            title: 'General',
            fields: [
                { label: 'Pronouns', value: profile?.pronoun },
                { label: 'Birthday', value: profile?.birthday },
                { label: 'Country', value: profile?.country },
            ],
        },
        {
            key: 'skin',
            title: 'Skin',
            fields: [
                { label: 'Skin Type', value: profile?.skinType },
                {
                    label: 'Skin Concerns',
                    value: skinConcernSelections.length > 0 ? skinConcernSelections : normalizeSkinConcernSelections(profile?.skinCondition),
                },
            ],
        },
        {
            key: 'hair',
            title: 'Hair',
            fields: [
                { label: 'Hair Texture', value: profile?.hairTexture },
                { label: 'Hair Density', value: profile?.hairDensity },
            ],
        },
    ];
    
    return (
        <div className="profile-view">
            {profile && (
                <section className="profile-info-card" aria-label="Profile information">
                    <div className="profile-info-card__title-row">
                        <h2 className="profile-info-card__title">Profile Info</h2>
                        <span className="profile-info-card__chip">Beauty Profile</span>
                    </div>

                    <div className="profile-info-groups">
                        {profileSections.map((section) => (
                            <section className="profile-info-group" key={section.key}>
                                <h3 className="profile-info-group__title">{section.title}</h3>
                                <div className={`profile-info-row profile-info-row--${section.key}`}>
                                    {section.fields.map((field) => (
                                        <article className="profile-info-item" key={field.label}>
                                            <p className="profile-info-item__label">{field.label}</p>
                                            {!isEditingProfile && (
                                                hasProfileValue(field.value) ? (
                                                    <p className="profile-info-item__value">{renderProfileValue(field.label, field.value)}</p>
                                                ) : isOwner ? (
                                                    <button
                                                        type="button"
                                                        className="profile-info-item__empty-action"
                                                        onClick={startEditingProfile}
                                                    >
                                                        {getAddProfileLabel(field.label)}
                                                    </button>
                                                ) : (
                                                    <p className="profile-info-item__value profile-info-item__value--empty">N/A</p>
                                                )
                                            )}
                                            {isEditingProfile && (
                                                field.label === 'Pronouns' ? (
                                                    <select
                                                        className="profile-info-item__select"
                                                        value={editDraft.pronouns}
                                                        onChange={(e) => setDraftField(field.label, e.target.value)}
                                                    >
                                                        <option value="">Select Pronouns</option>
                                                        <option value="SHE_HER">She/Her</option>
                                                        <option value="HE_HIM">He/Him</option>
                                                        <option value="THEY_THEM">They/Them</option>
                                                        <option value="OTHER">Other</option>
                                                    </select>
                                                ) : field.label === 'Birthday' ? (
                                                    <div className="profile-birthday-selects">
                                                        <select
                                                            className="profile-info-item__select"
                                                            value={editDraft.birthdayMonth}
                                                            onChange={(e) => setEditDraft((current) => ({ ...current, birthdayMonth: e.target.value }))}
                                                        >
                                                            <option value="">Month</option>
                                                            {MONTHS.map((month) => (
                                                                <option key={month.value} value={month.value}>
                                                                    {month.label}
                                                                </option>
                                                            ))}
                                                        </select>
                                                        <select
                                                            className="profile-info-item__select"
                                                            value={editDraft.birthdayDay}
                                                            onChange={(e) => setEditDraft((current) => ({ ...current, birthdayDay: e.target.value }))}
                                                        >
                                                            <option value="">Day</option>
                                                            {DAYS.map((day) => (
                                                                <option key={day.value} value={day.value}>
                                                                    {day.label}
                                                                </option>
                                                            ))}
                                                        </select>
                                                        <select
                                                            className="profile-info-item__select"
                                                            value={editDraft.birthdayYear}
                                                            onChange={(e) => setEditDraft((current) => ({ ...current, birthdayYear: e.target.value }))}
                                                        >
                                                            <option value="">Year</option>
                                                            {YEARS.map((year) => (
                                                                <option key={year.value} value={year.value}>
                                                                    {year.label}
                                                                </option>
                                                            ))}
                                                        </select>
                                                    </div>
                                                ) : field.label === 'Country' ? (
                                                    <select 
                                                        className="profile-info-item__select"
                                                        value={editDraft.country}
                                                        onChange={(e) => setDraftField(field.label, e.target.value)}
                                                    >
                                                        <option value="">Select Country</option>
                                                        <option value="USA">United States</option>
                                                        <option value="CANADA">Canada</option>
                                                        <option value="UK">United Kingdom</option>
                                                        <option value="AUSTRALIA">Australia</option>
                                                        <option value="OTHER">Other</option>
                                                    </select>
                                                ) : field.label === 'Skin Type' ? (
                                                    <select 
                                                        className="profile-info-item__select"
                                                        value={editDraft.skintype}
                                                        onChange={(e) => setDraftField(field.label, e.target.value)}
                                                    >
                                                        <option value="">Select Skin Type</option>
                                                        <option value="OILY">Oily</option>
                                                        <option value="DRY">Dry</option>
                                                        <option value="COMBINATION">Combination</option>
                                                        <option value="OTHER">Other</option>
                                                    </select>
                                                ) : field.label === 'Skin Concerns' ? (
                                                    <select
                                                        className="profile-info-item__select profile-info-item__select--multi"
                                                        value={editDraft.skincondition}
                                                        multiple
                                                        size={SKIN_CONCERN_OPTIONS.length}
                                                        onChange={(e) => {
                                                            const selectedValues = Array.from(e.target.selectedOptions, (option) => option.value);
                                                            setDraftField(field.label, selectedValues);
                                                        }}
                                                    >
                                                        {SKIN_CONCERN_OPTIONS.map((option) => (
                                                            <option key={option.value} value={option.value}>
                                                                {option.label}
                                                            </option>
                                                        ))}
                                                    </select>
                                                ) : field.label === 'Hair Texture' ? (
                                                    <select 
                                                        className="profile-info-item__select"
                                                        value={editDraft.hairtype}
                                                        onChange={(e) => setDraftField(field.label, e.target.value)}
                                                    >
                                                        <option value="">Select Hair Type</option>
                                                        <option value="STRAIGHT">Straight</option>
                                                        <option value="WAVY">Wavy</option>
                                                        <option value="CURLY">Curly</option>
                                                        <option value="COILY">Coily</option>
                                                        <option value="OTHER">Other</option>
                                                    </select>
                                                ) : field.label === 'Hair Density' ? (
                                                    <select 
                                                        className="profile-info-item__select"
                                                        value={editDraft.hairdensity}
                                                        onChange={(e) => setDraftField(field.label, e.target.value)}
                                                    >
                                                        <option value="">Select Hair Density</option>
                                                        <option value="THIN">Thin</option>
                                                        <option value="MEDIUM">Medium</option>
                                                        <option value="THICK">Thick</option>
                                                        <option value="OTHER">Other</option>
                                                    </select>
                                                ) : null
                                            )}
                                        </article>
                                    ))}
                                </div>
                            </section>
                        ))}
                    </div>
                </section>
            )}
            {isOwner && !isEditingProfile && (
                <button
                    type="button"
                    className="profile-edit-button profile-edit-button--edit"
                    onClick={startEditingProfile}
                >
                    Edit Profile
                </button>
            )}
            {isOwner && isEditingProfile && (
                <button
                    type="button"
                    className="profile-edit-button profile-edit-button--save"
                    onClick={handleEditProfile}
                >
                    Save Profile
                </button>
            )}
            <h1 className="profile-activities-title">{username}'s Activities</h1>

            {activities.length > 0 ? (
                <div className="feed-posts">
                    {activities.map((activity) => {
                        const payload = parseActivityPayload(activity.payload);
                        const message = payload.message ?? activity.payload ?? 'Activity update';
                        const imageUrl = payload?.media?.imageUrl ?? activity.imageUrl ?? null;
                        const imageAlt = payload?.productName
                            ? `${payload.productName}${payload?.shadeName ? ` - ${payload.shadeName}` : ''}`
                            : 'Activity media';
                        const key = activity.id ?? `${activity.type ?? 'activity'}-${activity.timestamp ?? message}`;

                        return (
                            <article className="feed-card" key={key}>
                                <div className="feed-card__header">
                                    <time className="feed-card__time" dateTime={activity.timestamp}>
                                        {activity.timestamp ? new Date(activity.timestamp).toLocaleString() : 'Recently'}
                                    </time>
                                </div>
                                <div style={{ display: 'flex', flexDirection: 'row', gap: '5px', alignItems: 'center' }}>
                                    <div style={{ display: 'flex', flexDirection: 'row', gap: '4px', flex: 1 }}>
                                        <p className="feed-card__actor" onClick={() => navigate(`/users/${activity.actorUsername}`)}>
                                            @{activity.actorUsername}
                                        </p>
                                        <p className="feed-card__message">{message}</p>
                                    </div>
                                    {imageUrl && (
                                        <div className="feed-card__media">
                                            <img src={imageUrl} alt={imageAlt} className="feed-card__image" draggable="false" />
                                        </div>
                                    )}
                                </div>
                            </article>
                        );
                    })}
                </div>
            ) : (
                <p className="profile-activities-empty">No activities yet.</p>
            )}
        </div>
    );
}