export async function fetchFeed() {
  try {
    const response = await fetch('/api/activities/following');
    if (!response.ok) {
      throw new Error('Failed to fetch feed');
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching feed:', error);
    throw error;
  }
}

export async function fetchUserActivities(username) {
  try {
    const response = await fetch(`/api/activities/${username}`);
    if (!response.ok) {
      throw new Error('Failed to fetch user activities');
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching user activities:', error);
    throw error;
  }
}