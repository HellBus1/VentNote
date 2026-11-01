-- ==============================================================================
-- VentNote SQLite Seed Data (Optimized for Android Studio App Inspector)
-- ==============================================================================
--
-- IMPORTANT FOR ANDROID STUDIO DATABASE INSPECTOR:
--   1. The database name in Database Inspector is: "backup" (NOT note_database).
--      Look for "backup" under process "com.digiventure.ventnote".
--   2. Android Studio Database Inspector executes ONE SQL statement per execution.
--      Do NOT run the entire file at once with BEGIN/COMMIT.
--   3. Run the 3 queries below one by one (or in 3 separate query tabs):
--        - Query 1: Seed Categories
--        - Query 2: Seed 7 Realistic Notes
--        - Query 3: Seed Category Associations
-- ==============================================================================


-- ==============================================================================
-- QUERY 1: SEED CATEGORIES (Copy and Run this first)
-- ==============================================================================
INSERT OR REPLACE INTO tag_table (id, name, color_hex) VALUES
  (1, 'Work & Career', '#90CAF9'),
  (2, 'Personal & Health', '#A5D6A7'),
  (3, 'Finance & Money', '#FFE082'),
  (4, 'Engineering & Code', '#CE93D8'),
  (5, 'Shopping & Errands', '#FFCC80'),
  (6, 'Travel & Adventure', '#80DEEA'),
  (7, 'Reading & Books', '#B39DDB'),
  (8, 'Home & Living', '#80CBC4'),
  (9, 'Urgent & Action', '#EF9A9A'),
  (10, 'Ideas & Brainstorm', '#F48FB1'),
  (11, 'Fitness & Training', '#FFAB91'),
  (12, 'Recipes & Food', '#C8E6C9');


-- ==============================================================================
-- QUERY 2: SEED 7 REALISTIC NOTES (Copy and Run this second)
-- ==============================================================================
INSERT OR REPLACE INTO note_table (id, title, note, created_at, updated_at, is_pinned) VALUES
  (1, 'Architecture RFC: Modular Navigation Flow', 'Proposal to decouple feature modules by introducing a centralized Navigation Router.' || char(10) || '' || char(10) || '```kotlin' || char(10) || 'interface FeatureNavigator {' || char(10) || '    fun openNoteDetail(navController: NavHostController, noteId: Int)' || char(10) || '    fun openTagPicker(navController: NavHostController)' || char(10) || '}' || char(10) || '```' || char(10) || '' || char(10) || '**Benefits:**' || char(10) || '1. Eliminates direct screen imports between circular feature packages.' || char(10) || '2. Simplifies unit testing with navigation mocks.', 1774674000000, 1774678000000, 0),
  (2, 'Book Notes: Designing Data-Intensive Applications', 'Key takeaways from Chapter 3 (Storage and Retrieval):' || char(10) || '- **SSTables & LSM-Trees:** Sequential write throughput is vastly superior to random writes on both spinning disks and SSDs.' || char(10) || '- **B-Trees:** The standard for relational databases (like SQLite and PostgreSQL), offering predictable O(log N) lookup times.' || char(10) || '- **WAL (Write-Ahead Logging):** SQLite uses WAL mode to enable concurrent readers while a write transaction commits.', 1785042000000, 1785045000000, 0),
  (3, 'Doctor Consultation & Prescription Directions', 'Annual Health Checkup Notes with Dr. Aris:' || char(10) || '- Blood pressure: 118/76 (Normal)' || char(10) || '- Fasting Glucose: 92 mg/dL (Healthy range)' || char(10) || '- Vitamin D levels slightly below optimal: Take 2,000 IU Vitamin D3 daily with breakfast.' || char(10) || '- Follow-up lipid panel scheduled in 6 months.', 1787634000000, 1787638000000, 0),
  (4, 'Emergency Contacts & Medical Info', '### Personal Details' || char(10) || '- Blood Type: **O Positive**' || char(10) || '- Allergies: Penicillin (Severe)' || char(10) || '' || char(10) || '### Emergency Contacts:' || char(10) || '1. Dr. Sarah Chen (Primary Physician): +1-555-0192' || char(10) || '2. Metro General Hospital Urgent Line: +1-555-0100' || char(10) || '3. Insurance Policy ID: #VENT-882910-A (Allianz Care)', 1789016400000, 1789020000000, 1),
  (5, 'Financial Allocation & Budget Q4', 'Monthly Income Allocation Strategy (50/30/20 Rule):' || char(10) || '- **50% Fixed Essentials:** Rent, utilities, high-speed fiber internet, groceries.' || char(10) || '- **30% Discretionary:** Dining out, subscriptions (JetBrains, Spotify, Gym), weekend trips.' || char(10) || '- **20% Long-Term Growth:** Index funds (Vanguard S&P 500 ETF) + High-Yield Savings emergency reserve.', 1789621200000, 1789628000000, 0),
  (6, 'Sprint Retrospective: 1.4.0 Release', '### What Went Well' || char(10) || '- Jetpack Compose M3 migration completed with zero visual regressions.' || char(10) || '- Room SQLite Many-to-Many tag association reduced lookup latency.' || char(10) || '- Google Drive backup now retains historical timestamps cleanly.' || char(10) || '' || char(10) || '### What Needs Improvement' || char(10) || '- Tablet drawer layout was overstretching before clamping to 320dp.' || char(10) || '- Need automated UI integration tests for TagChipBar horizontal drag.', 1789966800000, 1789972000000, 0),
  (7, 'URGENT: Server SSL Certificate Renewal', '## Action Required by EOD' || char(10) || '- Production SSL cert for api.ventnote.internal expires in 48 hours.' || char(10) || '- Coordinate with DevOps (Dmitri) to trigger Let''''s Encrypt renewal script.' || char(10) || '- Verify Nginx reload on reverse proxy without socket dropping.' || char(10) || '- Check mobile client pinning configuration to prevent handshake failures.', 1790226000000, 1790233200000, 1);


-- ==============================================================================
-- QUERY 3: SEED CATEGORY ASSOCIATIONS (Copy and Run this third)
-- ==============================================================================
INSERT OR REPLACE INTO note_tag_table (noteId, tagId) VALUES
  (1, 4),
  (1, 10),
  (2, 7),
  (2, 4),
  (3, 2),
  (4, 2),
  (4, 9),
  (5, 3),
  (5, 1),
  (5, 8),
  (6, 1),
  (6, 4),
  (6, 10),
  (7, 1),
  (7, 9),
  (7, 4);

-- Verification:
-- SELECT count(*) AS total_notes FROM note_table;     -- Expected: 7
-- SELECT count(*) AS total_tags FROM tag_table;       -- Expected: 12
-- SELECT count(*) AS total_links FROM note_tag_table; -- Expected: 16
