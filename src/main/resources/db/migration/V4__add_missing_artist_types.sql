-- Flyway Migration: V3 - Add Missing Artist Types
-- This migration adds all the missing artist types that should be available in the system

-- Insert all missing artist types
INSERT IGNORE INTO artist_types (name, display_name, description, icon_url, is_active, sort_order) VALUES
-- Actor/Actress
('ACTOR', 'Actor / Actress', 'Film, TV, and Theater Actors and Actresses', '/icons/actor.png', TRUE, 1),

-- Dancers & Choreographers  
('DANCER', 'Dancers & Choreographers', 'Professional Dancers and Choreographers specializing in various dance styles', '/icons/dancer.png', TRUE, 2),

-- Director
('DIRECTOR', 'Director', 'Film, TV, and Theater Directors', '/icons/director.png', TRUE, 3),

-- Writers
('WRITER', 'Writers', 'Script Writers, Content Writers, and Creative Writers', '/icons/writer.png', TRUE, 4),

-- Makeup and Hairdresser
('MAKEUP_HAIR', 'Makeup & Hairdresser', 'Professional Makeup Artists and Hairdressers', '/icons/makeup-hair.png', TRUE, 5),

-- Singers & Instrument Players
('SINGER', 'Singers & Instrument Players', 'Vocal Artists and Musicians', '/icons/singer.png', TRUE, 6),

-- Stand-up Comedian
('COMEDIAN', 'Stand-up Comedian', 'Stand-up Comedians and Comedy Artists', '/icons/comedian.png', TRUE, 7),

-- Band or Music Group
('BAND', 'Band or Music Group', 'Musical Bands and Music Groups', '/icons/band.png', TRUE, 8),

-- DJ / RJ
('DJ_RJ', 'DJ / RJ', 'Disc Jockeys and Radio Jockeys', '/icons/dj-rj.png', TRUE, 9),

-- Model
('MODEL', 'Model', 'Fashion and Commercial Models', '/icons/model.png', TRUE, 10),

-- Photographer
('PHOTOGRAPHER', 'Photographer', 'Professional Photographers', '/icons/photographer.png', TRUE, 11),

-- Videographer
('VIDEOGRAPHER', 'Videographer', 'Professional Videographers and Video Editors', '/icons/videographer.png', TRUE, 12),

-- Choreographer (separate from dancer)
('CHOREOGRAPHER', 'Choreographer', 'Professional Choreographers', '/icons/choreographer.png', TRUE, 13),

-- Music Producer
('MUSIC_PRODUCER', 'Music Producer', 'Music Producers and Sound Engineers', '/icons/music-producer.png', TRUE, 14),

-- Stunt Performer
('STUNT_PERFORMER', 'Stunt Performer', 'Professional Stunt Performers', '/icons/stunt-performer.png', TRUE, 15),

-- Voice Artist
('VOICE_ARTIST', 'Voice Artist', 'Voice Artists and Dubbing Artists', '/icons/voice-artist.png', TRUE, 16),

-- Comedian (different from stand-up)
('COMEDY_ARTIST', 'Comedy Artist', 'Comedy Artists and Humorists', '/icons/comedy-artist.png', TRUE, 17),

-- Event Manager
('EVENT_MANAGER', 'Event Manager', 'Event Managers and Event Planners', '/icons/event-manager.png', TRUE, 18),

-- Costume Designer
('COSTUME_DESIGNER', 'Costume Designer', 'Costume Designers and Fashion Stylists', '/icons/costume-designer.png', TRUE, 19),

-- Set Designer
('SET_DESIGNER', 'Set Designer', 'Set Designers and Production Designers', '/icons/set-designer.png', TRUE, 20),

-- Light Designer
('LIGHT_DESIGNER', 'Light Designer', 'Light Designers and Lighting Technicians', '/icons/light-designer.png', TRUE, 21),

-- Sound Designer
('SOUND_DESIGNER', 'Sound Designer', 'Sound Designers and Audio Engineers', '/icons/sound-designer.png', TRUE, 22),

-- Editor
('EDITOR', 'Editor', 'Video Editors and Post-Production Specialists', '/icons/editor.png', TRUE, 23),

-- Animator
('ANIMATOR', 'Animator', 'Animators and Motion Graphics Artists', '/icons/animator.png', TRUE, 24),

-- VFX Artist
('VFX_ARTIST', 'VFX Artist', 'Visual Effects Artists and Compositors', '/icons/vfx-artist.png', TRUE, 25),

-- Graphic Designer
('GRAPHIC_DESIGNER', 'Graphic Designer', 'Graphic Designers and Visual Artists', '/icons/graphic-designer.png', TRUE, 26),

-- Art Director
('ART_DIRECTOR', 'Art Director', 'Art Directors and Creative Directors', '/icons/art-director.png', TRUE, 27),

-- Production Manager
('PRODUCTION_MANAGER', 'Production Manager', 'Production Managers and Line Producers', '/icons/production-manager.png', TRUE, 28),

-- Assistant Director
('ASSISTANT_DIRECTOR', 'Assistant Director', 'Assistant Directors and ADs', '/icons/assistant-director.png', TRUE, 29),

-- Script Supervisor
('SCRIPT_SUPERVISOR', 'Script Supervisor', 'Script Supervisors and Continuity', '/icons/script-supervisor.png', TRUE, 30),

-- Casting Assistant
('CASTING_ASSISTANT', 'Casting Assistant', 'Casting Assistants and Casting Coordinators', '/icons/casting-assistant.png', TRUE, 31),

-- Location Manager
('LOCATION_MANAGER', 'Location Manager', 'Location Managers and Location Scouts', '/icons/location-manager.png', TRUE, 32),

-- Transportation Coordinator
('TRANSPORTATION_COORDINATOR', 'Transportation Coordinator', 'Transportation Coordinators and Logistics', '/icons/transportation-coordinator.png', TRUE, 33),

-- Catering Manager
('CATERING_MANAGER', 'Catering Manager', 'Catering Managers and Food Coordinators', '/icons/catering-manager.png', TRUE, 34),

-- Security Coordinator
('SECURITY_COORDINATOR', 'Security Coordinator', 'Security Coordinators and Safety Managers', '/icons/security-coordinator.png', TRUE, 35),

-- Public Relations Manager
('PR_MANAGER', 'Public Relations Manager', 'PR Managers and Marketing Coordinators', '/icons/pr-manager.png', TRUE, 36),

-- Social Media Manager
('SOCIAL_MEDIA_MANAGER', 'Social Media Manager', 'Social Media Managers and Content Creators', '/icons/social-media-manager.png', TRUE, 37),

-- Marketing Manager
('MARKETING_MANAGER', 'Marketing Manager', 'Marketing Managers and Brand Managers', '/icons/marketing-manager.png', TRUE, 38),

-- Sales Manager
('SALES_MANAGER', 'Sales Manager', 'Sales Managers and Business Development', '/icons/sales-manager.png', TRUE, 39),

-- Finance Manager
('FINANCE_MANAGER', 'Finance Manager', 'Finance Managers and Accountants', '/icons/finance-manager.png', TRUE, 40),

-- Legal Advisor
('LEGAL_ADVISOR', 'Legal Advisor', 'Legal Advisors and Entertainment Lawyers', '/icons/legal-advisor.png', TRUE, 41),

-- Talent Agent
('TALENT_AGENT', 'Talent Agent', 'Talent Agents and Artist Representatives', '/icons/talent-agent.png', TRUE, 42),

-- Manager
('MANAGER', 'Manager', 'Personal Managers and Artist Managers', '/icons/manager.png', TRUE, 43),

-- Publicist
('PUBLICIST', 'Publicist', 'Publicists and Media Relations', '/icons/publicist.png', TRUE, 44),

-- Coordinator
('COORDINATOR', 'Coordinator', 'General Coordinators and Project Managers', '/icons/coordinator.png', TRUE, 45),

-- Assistant
('ASSISTANT', 'Assistant', 'General Assistants and Support Staff', '/icons/assistant.png', TRUE, 46),

-- Intern
('INTERN', 'Intern', 'Interns and Trainees', '/icons/intern.png', TRUE, 47),

-- Volunteer
('VOLUNTEER', 'Volunteer', 'Volunteers and Support Staff', '/icons/volunteer.png', TRUE, 48),

-- Other
('OTHER', 'Other', 'Other Creative and Technical Roles', '/icons/other.png', TRUE, 49);
