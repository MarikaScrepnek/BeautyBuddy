import { API_BASE_URL } from '../../../config/apiBase';

export async function getBrands() {
  const res = await fetch(`${API_BASE_URL}/api/brands`);
    return res.json();
  }