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

export default function Profile({ username, isOwner }) {
    const [activities, setActivities] = useState([]);
    const navigate = useNavigate();
    const [profile, setProfile] = useState(null);
    const [isEditingProfile, setIsEditingProfile] = useState(false);

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
            setProfile(currentUser ?? null);
        } catch (error) {
            console.error('Error fetching profile:', error);
            setProfile(null);
        }
    };

    function handleEditProfile(pronouns, birthday, country, skintype, skincondition, hairtype, hairdensity) {
        editProfile(pronouns, birthday, country, skintype, skincondition, hairtype, hairdensity)
          .then((updatedUser) => {
            setUsername(updatedUser.username);
          })
          .catch((err) => console.error("Error updating profile:", err));
      }

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
                { label: 'Skin Condition', value: profile?.skinCondition },
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
                                            <p className="profile-info-item__value">{field.value || 'N/A'}</p>
                                            {isEditingProfile && (
                                                field.label === 'Pronouns' ? (
                                                    <select
                                                        type="pronouns"
                                                        placeholder="Pronouns (optional)"
                                                        value={pronoun}
                                                        onChange={(e) => setPronouns(e.target.value)}
                                                    >
                                                        <option value="">Select Pronouns</option>
                                                        <option value="SHE_HER">She/Her</option>
                                                        <option value="HE_HIM">He/Him</option>
                                                        <option value="THEY_THEM">They/Them</option>
                                                        <option value="OTHER">Other</option>
                                                    </select>
                                                ) : field.label === 'Country' ? (
                                                    <select 
                                                        type="country" 
                                                        placeholder="Country (optional)" 
                                                        value={country}
                                                        onChange={(e) => setCountry(e.target.value)}
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
                                                        type="skintype" 
                                                        placeholder="Skin Type (optional)" 
                                                        value={skintype}
                                                        onChange={(e) => setSkintype(e.target.value)}
                                                    >
                                                        <option value="">Select Skin Type</option>
                                                        <option value="OILY">Oily</option>
                                                        <option value="DRY">Dry</option>
                                                        <option value="COMBINATION">Combination</option>
                                                        <option value="OTHER">Other</option>
                                                    </select>
                                                ) : field.label === 'Skin Condition' ? (
                                                    <select
                                                        type="skincondition"
                                                        placeholder="Skin Condition (optional)"
                                                        value={skincondition}
                                                        onChange={(e) => setSkincondition(e.target.value)}
                                                    >
                                                        <option value="">Select Skin Condition</option>
                                                        <option value="NORMAL">Normal</option>
                                                        <option value="SENSITIVE">Sensitive</option>
                                                        <option value="ACNE_PRONE">Acne Prone</option>
                                                    </select>
                                                ) : field.label === 'Hair Texture' ? (
                                                    <select 
                                                        type="hairtype" 
                                                        placeholder="Hair Type (optional)" 
                                                        value={hairtype}
                                                        onChange={(e) => setHairtype(e.target.value)}
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
                                                        type="hairdensity" 
                                                        placeholder="Hair Density (optional)" 
                                                        value={hairdensity}
                                                        onChange={(e) => setHairdensity(e.target.value)}
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
                    onClick={() => setIsEditingProfile(true)}
                >
                    Edit Profile
                </button>
            )}
            {isOwner && isEditingProfile && (
                <button
                    type="button"
                    className="profile-edit-button profile-edit-button--save"
                    onClick={() => setIsEditingProfile(false)}
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