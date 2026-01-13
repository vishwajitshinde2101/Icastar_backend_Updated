#!/bin/bash

# ============================================
# ARTIST DASHBOARD APIs - TEST SCRIPT
# ============================================

# Set your JWT token here
JWT_TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkQGIuY29tIiwiaWF0IjoxNzY4MjM3NjEzLCJleHAiOjE3NjgzMjQwMTN9.QWf-k-8On3Oz6uxQiFZnmrmeH6w7vIhabOCrTd33eglVVcwqeWinQfBfOslWE9nwfDPguVp6ShyHVnKhKX0qbQ"
BASE_URL="https://app.icastar.com/api"

echo "============================================"
echo "TESTING ARTIST DASHBOARD APIs"
echo "============================================"
echo ""

# 1. Get Dashboard Metrics (6 KPIs with trends)
echo "1. Testing Dashboard Metrics..."
curl -s "$BASE_URL/artist/dashboard/metrics" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

# 2. Get Job Opportunities (AI-matched jobs)
echo "2. Testing Job Opportunities..."
curl -s "$BASE_URL/artist/dashboard/job-opportunities?limit=10" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

# 3. Get Profile Views Trend (7 months)
echo "3. Testing Profile Views Trend..."
curl -s "$BASE_URL/artist/dashboard/profile-views-trend" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

# 4. Get Application Status Breakdown
echo "4. Testing Application Status..."
curl -s "$BASE_URL/artist/dashboard/application-status" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

# 5. Get Earnings Trend (7 months)
echo "5. Testing Earnings Trend..."
curl -s "$BASE_URL/artist/dashboard/earnings-trend?period=30" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

# 6. Get Portfolio Items
echo "6. Testing Portfolio..."
curl -s "$BASE_URL/artist/dashboard/portfolio?limit=6" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

# 7. Get Recent Activity Timeline
echo "7. Testing Recent Activity..."
curl -s "$BASE_URL/artist/dashboard/recent-activity?limit=10" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

# 8. My Applications
echo "8. Testing My Applications..."
curl -s "$BASE_URL/my-applications" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Accept: application/json"
echo -e "\n---\n"

echo "============================================"
echo "ALL TESTS COMPLETED"
echo "============================================"
