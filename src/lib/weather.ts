import { LocationState } from './types';

// Preset Locations in Tamil Nadu as fallbacks
export const TAMIL_NADU_LOCATIONS = [
  { name: 'SRM Kattankulathur', district: 'Chengalpattu, TN', lat: 12.8231, lng: 80.0442 },
  { name: 'Thanjavur Delta', district: 'Thanjavur, TN', lat: 10.7870, lng: 79.1378 },
  { name: 'Tiruchirappalli (Trichy)', district: 'Tiruchirappalli, TN', lat: 10.7905, lng: 78.7047 },
  { name: 'Chennai Central', district: 'Chennai, TN', lat: 13.0827, lng: 80.2707 },
  { name: 'Coimbatore South', district: 'Coimbatore, TN', lat: 11.0168, lng: 76.9558 },
  { name: 'Madurai Heritage', district: 'Madurai, TN', lat: 9.9252, lng: 78.1198 }
];

/**
 * Reverse geocode latitude and longitude to get the exact locality, town, or city name
 */
export async function getPlaceNameFromCoords(lat: number, lng: number): Promise<{ locationName: string; district: string }> {
  // Try BigDataCloud reverse geocode client API (fast, free, no CORS issue)
  try {
    const res = await fetch(
      `https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=${lat}&longitude=${lng}&localityLanguage=en`
    );
    if (res.ok) {
      const data = await res.json();
      const city = data.city || data.locality || data.localityInfo?.informative?.[0]?.name || data.principalSubdivision;
      const district = [data.principalSubdivision, data.countryName].filter(Boolean).join(', ');
      if (city) {
        return {
          locationName: city,
          district: district || 'Detected Region'
        };
      }
    }
  } catch (e) {
    console.warn('BigDataCloud reverse geocode error:', e);
  }

  // Fallback to OpenStreetMap Nominatim
  try {
    const res2 = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`);
    if (res2.ok) {
      const data2 = await res2.json();
      const addr = data2.address || {};
      const city = addr.city || addr.town || addr.village || addr.suburb || addr.county || addr.state_district || 'Detected Place';
      const state = [addr.state || addr.state_district, addr.country].filter(Boolean).join(', ');
      return {
        locationName: city,
        district: state || 'GPS Coordinates'
      };
    }
  } catch (e) {
    console.warn('Nominatim reverse geocode error:', e);
  }

  return {
    locationName: `GPS (${lat.toFixed(3)}°, ${lng.toFixed(3)}°)`,
    district: 'Live Position'
  };
}

export async function fetchLiveWeather(
  lat: number,
  lng: number,
  customName?: string,
  customDistrict?: string
): Promise<LocationState> {
  let resolvedName = customName;
  let resolvedDistrict = customDistrict;

  // If customName is missing or generic, perform reverse geocoding to find exact current location!
  if (!resolvedName || resolvedName === 'My Detected GPS Location' || resolvedName === 'GPS Location') {
    const geocoded = await getPlaceNameFromCoords(lat, lng);
    resolvedName = geocoded.locationName;
    resolvedDistrict = geocoded.district;
  }

  try {
    const res = await fetch(
      `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lng}&current_weather=true&hourly=relative_humidity_2m`
    );
    const data = await res.json();
    
    const currentWeather = data.current_weather || {};
    const code = currentWeather.weathercode ?? 0;
    
    const weatherCondition = getWeatherText(code);

    return {
      permissionGranted: true,
      lat,
      lng,
      locationName: resolvedName,
      district: resolvedDistrict || 'Tamil Nadu',
      temperature: currentWeather.temperature ?? 29.5,
      weatherCondition,
      windSpeedKm: currentWeather.windspeed ?? 12.4,
      humidityPct: data.hourly?.relative_humidity_2m?.[0] ?? 64,
      lastUpdatedTime: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
    };
  } catch (e) {
    // Fallback live object
    return {
      permissionGranted: true,
      lat,
      lng,
      locationName: resolvedName || 'SRM Kattankulathur',
      district: resolvedDistrict || 'Chengalpattu, TN',
      temperature: 30.2,
      weatherCondition: 'Partly Cloudy · Tamil Nadu Coastal Breeze',
      windSpeedKm: 14.2,
      humidityPct: 68,
      lastUpdatedTime: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
    };
  }
}

/**
 * IP-based geolocation fallback when browser GPS permission is denied or restricted in preview iFrames
 */
export async function fetchLocationByIP(): Promise<LocationState | null> {
  try {
    const res = await fetch('https://ipapi.co/json/');
    if (res.ok) {
      const data = await res.json();
      if (data.latitude && data.longitude) {
        return await fetchLiveWeather(
          data.latitude,
          data.longitude,
          data.city || data.region,
          `${data.region || ''}, ${data.country_name || ''}`.trim()
        );
      }
    }
  } catch (e) {
    console.warn('IP geolocation error:', e);
  }

  // Backup IP API
  try {
    const res2 = await fetch('https://ip-api.com/json/');
    if (res2.ok) {
      const data2 = await res2.json();
      if (data2.lat && data2.lon) {
        return await fetchLiveWeather(
          data2.lat,
          data2.lon,
          data2.city || data2.regionName,
          `${data2.regionName || ''}, ${data2.country || ''}`.trim()
        );
      }
    }
  } catch (e) {
    console.warn('Backup IP geolocation error:', e);
  }

  return null;
}

/**
 * Search locations by custom name query
 */
export async function searchLocationByName(query: string): Promise<{ name: string; district: string; lat: number; lng: number }[]> {
  if (!query || query.trim().length < 2) return [];
  try {
    const res = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=5`);
    if (res.ok) {
      const data = await res.json();
      return data.map((item: any) => ({
        name: item.display_name.split(',')[0],
        district: item.display_name.split(',').slice(1, 3).join(',').trim(),
        lat: parseFloat(item.lat),
        lng: parseFloat(item.lon)
      }));
    }
  } catch (e) {
    console.warn('Search location error:', e);
  }
  return [];
}

function getWeatherText(code: number): string {
  if (code === 0) return 'Clear Sunny Skies';
  if (code === 1 || code === 2) return 'Partly Cloudy · Pleasant';
  if (code === 3) return 'Overcast Skies';
  if (code >= 51 && code <= 65) return 'Light Tropical Drizzle';
  if (code >= 80 && code <= 99) return 'Thunderstorm Activity · Rain Alert';
  return 'Humid Coastal Tropical Weather';
}
