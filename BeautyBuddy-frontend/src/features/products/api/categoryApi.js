import { API_BASE_URL } from '../../../config/apiBase';

export async function getCategories() {
  const res = await fetch(`${API_BASE_URL}/api/categories`);
    return res.json();
  }