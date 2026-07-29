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
                                                <input
                                                    type="text"
                                                    className="profile-info-item__input"
                                                    placeholder={`Enter ${field.label.toLowerCase()}`}
                                                />
                                            )}
                                        </article>
                                    ))}
                                </div>
                            </section>
                        ))}
                    </div>
                </section>
            )}
            {isOwner && (
                <button className="profile-edit-button" onClick={() => setIsEditingProfile(true)}>
                    Edit Profile
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