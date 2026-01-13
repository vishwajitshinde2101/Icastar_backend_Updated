# Artist Dashboard API CURL Commands

## Setup
```bash
JWT_TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkQGIuY29tIiwiaWF0IjoxNzY4MjM3NjEzLCJleHAiOjE3NjgzMjQwMTN9.QWf-k-8On3Oz6uxQiFZnmrmeH6w7vIhabOCrTd33eglVVcwqeWinQfBfOslWE9nwfDPguVp6ShyHVnKhKX0qbQ"
```

---

## 1. Dashboard Metrics
Get 6 KPIs with monthly trends (Profile Views, Job Invitations, Applications, Interviews, Projects, Earnings)

```bash
curl -s "https://app.icastar.com/api/artist/dashboard/metrics" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "profileViews": { "value": 0, "trend": 0.0, "label": "Profile Views" },
    "jobInvitations": { "value": 0, "trend": 0.0, "label": "Job Invitations" },
    "applicationsSent": { "value": 0, "trend": 0.0, "label": "Applications Sent" },
    "interviewsScheduled": { "value": 0, "trend": 0.0, "label": "Interviews Scheduled" },
    "projectsCompleted": { "value": 0, "trend": 0.0, "label": "Projects Completed" },
    "creditsBalance": { "value": 0.0, "trend": 0.0, "label": "Credits Balance", "currency": "INR" }
  }
}
```

---

## 2. Job Opportunities
Get AI-matched job opportunities based on artist profile

```bash
curl -s "https://app.icastar.com/api/artist/dashboard/job-opportunities?limit=10" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Parameters:**
- `limit` (default: 10) - Number of jobs to return

---

## 3. Profile Views Trend
Get profile views trend for last 7 months

```bash
curl -s "https://app.icastar.com/api/artist/dashboard/profile-views-trend" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "months": ["JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"],
    "views": [0, 0, 0, 0, 0, 0, 0],
    "totalViews": 0,
    "averageViews": 0.0,
    "peakMonth": "N/A"
  }
}
```

---

## 4. Application Status Breakdown
Get breakdown of applications by status

```bash
curl -s "https://app.icastar.com/api/artist/dashboard/application-status" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "labels": ["Pending", "Under Review", "Shortlisted", "Interviewing", "Offered", "Hired", "Rejected"],
    "data": [0, 0, 0, 0, 0, 0, 0],
    "total": 0
  }
}
```

---

## 5. Earnings Trend
Get earnings trend for last 7 months

```bash
curl -s "https://app.icastar.com/api/artist/dashboard/earnings-trend?period=30" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "months": ["JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"],
    "earnings": [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],
    "totalEarnings": 0.0,
    "averageEarnings": 0.0,
    "highestMonth": "N/A",
    "currency": "INR"
  }
}
```

---

## 6. Portfolio Items
Get artist's portfolio items

```bash
curl -s "https://app.icastar.com/api/artist/dashboard/portfolio?limit=6" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Parameters:**
- `limit` (default: 6) - Number of portfolio items to return

---

## 7. Recent Activity
Get recent activity timeline

```bash
curl -s "https://app.icastar.com/api/artist/dashboard/recent-activity?limit=10" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Parameters:**
- `limit` (default: 10) - Number of activities to return

---

## 8. My Applications
Get all job applications for the artist

```bash
curl -s "https://app.icastar.com/api/my-applications" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

**Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `status` - Filter by status (APPLIED, UNDER_REVIEW, SHORTLISTED, etc.)
- `jobTitle` - Search by job title
- `companyName` - Search by company name
- `sortBy` (default: appliedAt) - Sort field
- `sortDir` (default: desc) - Sort direction (asc/desc)

**Example with filters:**
```bash
curl -s "https://app.icastar.com/api/my-applications?page=0&size=20&status=SHORTLISTED&sortBy=appliedAt&sortDir=desc" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json" | jq '.'
```

---

## Run All Tests

**Using the test script:**
```bash
chmod +x test-dashboard-apis.sh
./test-dashboard-apis.sh
```

**Or run them individually:**
```bash
# Set token once
export JWT_TOKEN="your-token-here"

# Then run any API
curl -s "https://app.icastar.com/api/artist/dashboard/metrics" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq '.'
```

---

## Notes

1. **Authentication Required:** All endpoints require a valid JWT token
2. **Artist Role Required:** User must have ARTIST role
3. **Artist Profile Required:** Artist profile must be created first
4. **Production URL:** `https://app.icastar.com/api`
5. **Local Testing:** Replace URL with `http://localhost:8080/api`

---

## Troubleshooting

**401 Unauthorized:**
- Check if JWT token is valid and not expired
- Ensure token is properly set in Authorization header

**403 Forbidden:**
- User role must be ARTIST
- Check if user is logged in with correct role

**404 Artist Profile Not Found:**
- Create artist profile first using `/api/artists/profile`

**500 Internal Server Error:**
- Check if production has latest code deployed
- Check application logs for specific error
