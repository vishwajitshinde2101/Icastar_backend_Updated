#!/bin/bash

# Audition Management System API Test Commands
# Replace YOUR_JWT_TOKEN with a valid JWT token for a recruiter user
# Replace IDs (1, 2, etc.) with actual IDs from your database

BASE_URL="http://localhost:8080/api/recruiter/auditions"
TOKEN="YOUR_JWT_TOKEN"

echo "=== AUDITION MANAGEMENT SYSTEM API TESTS ==="
echo ""

# ============================================
# 1. GET /api/recruiter/auditions - List all casting calls with filtering
# ============================================
echo "1. List all casting calls (with filters):"
curl -X GET "${BASE_URL}?searchTerm=actor&status=OPEN&page=0&size=20" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 2. GET /api/recruiter/auditions/:id - Get single casting call
# ============================================
echo "2. Get single casting call:"
curl -X GET "${BASE_URL}/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 3. POST /api/recruiter/auditions - Create new casting call
# ============================================
echo "3. Create new casting call:"
curl -X POST "${BASE_URL}" \
  -H "Authorization: Bearer ${TOKEN}" \
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
  }' | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 4. PUT /api/recruiter/auditions/:id - Update casting call
# ============================================
echo "4. Update casting call:"
curl -X PUT "${BASE_URL}/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Lead Actor for Feature Film - UPDATED",
    "description": "Updated description",
    "isUrgent": true
  }' | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 5. DELETE /api/recruiter/auditions/:id - Delete draft casting call
# ============================================
echo "5. Delete draft casting call:"
curl -X DELETE "${BASE_URL}/2" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 6. POST /api/recruiter/auditions/:id/publish - Publish casting call
# ============================================
echo "6. Publish casting call:"
curl -X POST "${BASE_URL}/1/publish" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 7. POST /api/recruiter/auditions/:id/close - Close casting call
# ============================================
echo "7. Close casting call:"
curl -X POST "${BASE_URL}/1/close" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 8. GET /api/recruiter/auditions/:id/applications - List applications
# ============================================
echo "8. List applications for a casting call:"
curl -X GET "${BASE_URL}/1/applications?status=APPLIED&isShortlisted=false&page=0&size=20" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 9. GET /api/recruiter/auditions/:id/applications/:appId - Get single application
# ============================================
echo "9. Get single application:"
curl -X GET "${BASE_URL}/1/applications/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 10. PUT /api/recruiter/auditions/:id/applications/:appId/status - Update application status
# ============================================
echo "10. Update application status to SHORTLISTED:"
curl -X PUT "${BASE_URL}/1/applications/1/status" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SHORTLISTED",
    "notes": "Impressive audition, great fit for the role",
    "rating": 5
  }' | python3 -m json.tool

echo -e "\n\n"

echo "10b. Update application status to CALLBACK_SCHEDULED:"
curl -X PUT "${BASE_URL}/1/applications/1/status" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CALLBACK_SCHEDULED",
    "callbackDate": "2024-02-20T14:00:00",
    "callbackLocation": "Studio A, Los Angeles",
    "callbackNotes": "Please prepare monologue from script pages 5-7"
  }' | python3 -m json.tool

echo -e "\n\n"

echo "10c. Update application status to SELECTED:"
curl -X PUT "${BASE_URL}/1/applications/1/status" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SELECTED",
    "feedback": "Excellent performance, perfect for the role"
  }' | python3 -m json.tool

echo -e "\n\n"

echo "10d. Update application status to REJECTED:"
curl -X PUT "${BASE_URL}/1/applications/2/status" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "REJECTED",
    "rejectionReason": "Not the right fit for this particular role",
    "feedback": "Thank you for your submission"
  }' | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 11. POST /api/recruiter/auditions/:id/applications/bulk-update - Bulk status update
# ============================================
echo "11. Bulk update application statuses:"
curl -X POST "${BASE_URL}/1/applications/bulk-update" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "applicationIds": [3, 4, 5, 6],
    "status": "UNDER_REVIEW",
    "notes": "Moving to review stage"
  }' | python3 -m json.tool

echo -e "\n\n"

echo "11b. Bulk reject applications:"
curl -X POST "${BASE_URL}/1/applications/bulk-update" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "applicationIds": [7, 8, 9],
    "status": "REJECTED",
    "rejectionReason": "Position filled",
    "notes": "Thank you for your interest"
  }' | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 12. PUT /api/recruiter/auditions/:id/applications/:appId/notes - Add notes
# ============================================
echo "12. Add notes to application:"
curl -X PUT "${BASE_URL}/1/applications/1/notes" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Follow up: Discussed contract details with agent",
    "rating": 5
  }' | python3 -m json.tool

echo -e "\n\n"

# ============================================
# 13. GET /api/recruiter/auditions/stats - Get statistics
# ============================================
echo "13. Get statistics:"
curl -X GET "${BASE_URL}/stats" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" | python3 -m json.tool

echo -e "\n\n"

echo "=== ALL TESTS COMPLETED ==="
