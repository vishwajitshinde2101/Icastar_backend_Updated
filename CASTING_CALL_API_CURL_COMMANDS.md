# Audition Management System - cURL Commands

Replace `YOUR_JWT_TOKEN` with your actual JWT token for a recruiter user.
Replace IDs (`1`, `2`, etc.) with actual IDs from your database.

---

## 1. List All Casting Calls (with filters)

```bash
curl -X GET "http://localhost:8080/api/recruiter/auditions?searchTerm=actor&status=OPEN&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Available filters:**
- `searchTerm` - Search in title, description, character name
- `status` - DRAFT, OPEN, CLOSED, CANCELLED
- `roleType` - LEAD, SUPPORTING, BACKGROUND, EXTRA
- `projectType` - FEATURE_FILM, TV_SERIES, COMMERCIAL, THEATER, WEB_SERIES, SHORT_FILM, MUSIC_VIDEO
- `location` - Location string
- `isUrgent` - true/false
- `isFeatured` - true/false
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)

---

## 2. Get Single Casting Call

```bash
curl -X GET "http://localhost:8080/api/recruiter/auditions/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

---

## 3. Create New Casting Call

```bash
curl -X POST "http://localhost:8080/api/recruiter/auditions" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Lead Actor for Feature Film",
    "description": "Seeking experienced actor for lead role in upcoming feature film",
    "characterName": "John Smith",
    "characterDescription": "Middle-aged detective with a troubled past",
    "projectTitle": "The Last Detective",
    "projectType": "FEATURE_FILM",
    "productionCompany": "ABC Productions",
    "director": "Jane Doe",
    "castingDirector": "Bob Wilson",
    "roleType": "LEAD",
    "genderPreference": "MALE",
    "ageRangeMin": 35,
    "ageRangeMax": 50,
    "ethnicityPreference": "ANY",
    "physicalRequirements": "Fit, able to perform light stunts",
    "skillsRequired": ["Acting", "Stage Combat", "Driving"],
    "experienceRequired": "5+ years professional acting experience",
    "auditionFormat": "IN_PERSON",
    "auditionLocation": "Los Angeles, CA",
    "auditionDate": "2024-02-15T10:00:00",
    "callbackDate": "2024-02-20T14:00:00",
    "shootingStartDate": "2024-03-01",
    "shootingEndDate": "2024-05-31",
    "compensation": "SAG Scale + Backend",
    "compensationType": "PAID",
    "isUnionProject": true,
    "applicationDeadline": "2024-02-10T23:59:59",
    "requiredDocuments": ["Resume", "Headshot", "Demo Reel"],
    "submissionInstructions": "Please submit your materials via the platform",
    "contactEmail": "casting@abcproductions.com",
    "contactPhone": "+1-555-0123",
    "additionalNotes": "We are looking for someone with strong dramatic range",
    "isUrgent": false,
    "isFeatured": true,
    "allowVideoSubmissions": true,
    "requireCoverLetter": true
  }'
```

---

## 4. Update Casting Call

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Lead Actor for Feature Film - UPDATED",
    "description": "Updated description",
    "isUrgent": true
  }'
```

**Note:** All fields are optional for updates. Only include fields you want to change.

---

## 5. Delete Draft Casting Call

```bash
curl -X DELETE "http://localhost:8080/api/recruiter/auditions/2" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Note:** Only DRAFT casting calls can be deleted.

---

## 6. Publish Casting Call

```bash
curl -X POST "http://localhost:8080/api/recruiter/auditions/1/publish" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Note:** Changes status from DRAFT to OPEN. Validates required fields before publishing.

---

## 7. Close Casting Call

```bash
curl -X POST "http://localhost:8080/api/recruiter/auditions/1/close" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Note:** Changes status from OPEN to CLOSED.

---

## 8. List Applications for Casting Call

```bash
curl -X GET "http://localhost:8080/api/recruiter/auditions/1/applications?status=APPLIED&isShortlisted=false&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Available filters:**
- `status` - APPLIED, UNDER_REVIEW, SHORTLISTED, CALLBACK_SCHEDULED, CALLBACK_COMPLETED, SELECTED, REJECTED, WITHDRAWN
- `isShortlisted` - true/false
- `minRating` - Minimum rating (1-5)
- `page` - Page number
- `size` - Page size

---

## 9. Get Single Application

```bash
curl -X GET "http://localhost:8080/api/recruiter/auditions/1/applications/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

---

## 10. Update Application Status

### a. Move to UNDER_REVIEW

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1/applications/1/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "UNDER_REVIEW",
    "notes": "Reviewing application"
  }'
```

### b. Move to SHORTLISTED

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1/applications/1/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SHORTLISTED",
    "notes": "Impressive audition, great fit for the role",
    "rating": 5
  }'
```

### c. Schedule CALLBACK

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1/applications/1/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CALLBACK_SCHEDULED",
    "callbackDate": "2024-02-20T14:00:00",
    "callbackLocation": "Studio A, Los Angeles",
    "callbackNotes": "Please prepare monologue from script pages 5-7"
  }'
```

### d. Mark Callback COMPLETED

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1/applications/1/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CALLBACK_COMPLETED",
    "notes": "Callback went well"
  }'
```

### e. SELECT Candidate

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1/applications/1/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SELECTED",
    "feedback": "Excellent performance, perfect for the role"
  }'
```

### f. REJECT Candidate

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1/applications/2/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "REJECTED",
    "rejectionReason": "Not the right fit for this particular role",
    "feedback": "Thank you for your submission"
  }'
```

**Status Transition Rules:**
- APPLIED → UNDER_REVIEW, REJECTED, WITHDRAWN
- UNDER_REVIEW → SHORTLISTED, REJECTED, WITHDRAWN
- SHORTLISTED → CALLBACK_SCHEDULED, SELECTED, REJECTED, WITHDRAWN
- CALLBACK_SCHEDULED → CALLBACK_COMPLETED, WITHDRAWN
- CALLBACK_COMPLETED → SELECTED, REJECTED
- SELECTED, REJECTED, WITHDRAWN → (Final states)

---

## 11. Bulk Update Application Statuses

### a. Bulk move to UNDER_REVIEW

```bash
curl -X POST "http://localhost:8080/api/recruiter/auditions/1/applications/bulk-update" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "applicationIds": [3, 4, 5, 6],
    "status": "UNDER_REVIEW",
    "notes": "Moving to review stage"
  }'
```

### b. Bulk REJECT applications

```bash
curl -X POST "http://localhost:8080/api/recruiter/auditions/1/applications/bulk-update" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "applicationIds": [7, 8, 9],
    "status": "REJECTED",
    "rejectionReason": "Position filled",
    "notes": "Thank you for your interest"
  }'
```

**Response includes:**
- `totalRequested` - Number of applications requested to update
- `successful` - Number successfully updated
- `failed` - Number that failed
- `errorMessages` - List of error messages
- `successfulIds` - List of successful application IDs
- `failedIds` - List of failed application IDs

---

## 12. Add Notes to Application

```bash
curl -X PUT "http://localhost:8080/api/recruiter/auditions/1/applications/1/notes" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Follow up: Discussed contract details with agent",
    "rating": 5
  }'
```

**Note:** Notes are appended with timestamp, not overwritten.

---

## 13. Get Statistics

```bash
curl -X GET "http://localhost:8080/api/recruiter/auditions/stats" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Returns:**
- Total casting calls (by status)
- Total applications (by status)
- Selection rate
- Average applications per casting call
- Recent applications (last 30 days)
- Status breakdown

---

## Quick Test Sequence

To quickly test the full workflow:

```bash
# Set your token
export TOKEN="YOUR_JWT_TOKEN"

# 1. Create a casting call
curl -X POST "http://localhost:8080/api/recruiter/auditions" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Audition","description":"Test","projectType":"FEATURE_FILM","roleType":"LEAD"}'

# 2. Get the ID from response, then publish it (replace 1 with actual ID)
curl -X POST "http://localhost:8080/api/recruiter/auditions/1/publish" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# 3. List all casting calls
curl -X GET "http://localhost:8080/api/recruiter/auditions" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# 4. Get statistics
curl -X GET "http://localhost:8080/api/recruiter/auditions/stats" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

---

## Response Format

### Success Response
```json
{
  "success": true,
  "message": "Optional success message",
  "data": { ... },
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "size": 20
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description"
}
```

---

## Notes

1. All endpoints require a valid JWT token with RECRUITER role
2. Recruiters can only access their own casting calls
3. IDs in examples (1, 2, etc.) need to be replaced with actual IDs from your database
4. Date format: ISO 8601 (e.g., "2024-02-15T10:00:00")
5. Pagination defaults: page=0, size=20
6. Status transitions are validated - invalid transitions will return an error
