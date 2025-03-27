# Mission Summary
Develop an offline-first app to display an image post list from an API.
- The UI should automatically update by observing the local database.
- Sort and filter the data according to the likes and comments fields.

# Debated Issues
### 1. Data Sorting:
- The API does not support sorting or filtering by likes and comments, so this must be handled locally.

# Possible Solutions
### Full Data Replacement:
- Fetch all data once a day using WorkManager (single call to replace the entire database table).
- Push data to the database and query it in a sorted order by observing the query.

### Pagination Approach:
- Local database flow can handle pagination on API requests efficiently.
- However, filtering and sorting locally would cause inconsistent UX, as the list order would shift with every API fetch.

# Ideal Solution
Given the API data structure (light JSON with image URLs), **Full Data Replacement** is the best approach:
- **Daily Fetch**:
    - Use WorkManager to pull data once a day from the API.
    - Replace the local database with the updated data.
    - Use LazyColumn and Coil for efficient rendering and image loading.

- **Second Fetch Handling**:
    - When displayed data is nearly exhausted, trigger a new API fetch with large-page pagination to avoid duplicate data.
    - Update the local database with the latest data, including sorting and filtering.
    - **Trade-off**: List position may reset after each new fetch to support consistent sorting, but this will be rare after the initial large fetch.
