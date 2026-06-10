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