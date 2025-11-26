package com.icastar.platform.config;

import com.icastar.platform.entity.ArtistType;
import com.icastar.platform.entity.ArtistTypeField;
import com.icastar.platform.entity.FieldType;
import com.icastar.platform.repository.ArtistTypeRepository;
import com.icastar.platform.repository.ArtistTypeFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArtistTypeDataInitializer implements CommandLineRunner {

    private final ArtistTypeRepository artistTypeRepository;
    private final ArtistTypeFieldRepository artistTypeFieldRepository;

    @Override
    public void run(String... args) throws Exception {
        if (artistTypeRepository.count() == 0) {
            log.info("Initializing artist types and fields...");
            initializeArtistTypes();
            log.info("Artist types and fields initialized successfully!");
        }
    }

    private void initializeArtistTypes() {
        // Actor
        ArtistType actor = createArtistType("ACTOR", "Actor", "Film, TV, and Theater Actors", 1);
        actor = artistTypeRepository.save(actor);
        createActorFields(actor);

        // Dancer
        ArtistType dancer = createArtistType("DANCER", "Dancer", "Professional Dancers", 2);
        dancer = artistTypeRepository.save(dancer);
        createDancerFields(dancer);

        // Singer
        ArtistType singer = createArtistType("SINGER", "Singer", "Vocal Artists and Musicians", 3);
        singer = artistTypeRepository.save(singer);
        createSingerFields(singer);

        // Director
        ArtistType director = createArtistType("DIRECTOR", "Director", "Film, TV, and Theater Directors", 4);
        director = artistTypeRepository.save(director);
        createDirectorFields(director);

        // Writer
        ArtistType writer = createArtistType("WRITER", "Writer", "Script Writers, Content Writers", 5);
        writer = artistTypeRepository.save(writer);
        createWriterFields(writer);

        // DJ/RJ
        ArtistType dj = createArtistType("DJ_RJ", "DJ/RJ", "Disc Jockeys and Radio Jockeys", 6);
        dj = artistTypeRepository.save(dj);
        createDjFields(dj);

        // Band
        ArtistType band = createArtistType("BAND", "Band", "Musical Bands and Groups", 7);
        band = artistTypeRepository.save(band);
        createBandFields(band);

        // Model
        ArtistType model = createArtistType("MODEL", "Model", "Fashion and Commercial Models", 8);
        model = artistTypeRepository.save(model);
        createModelFields(model);

        // Photographer
        ArtistType photographer = createArtistType("PHOTOGRAPHER", "Photographer", "Professional Photographers", 9);
        photographer = artistTypeRepository.save(photographer);
        createPhotographerFields(photographer);

        // Videographer
        ArtistType videographer = createArtistType("VIDEOGRAPHER", "Videographer", "Video Production Specialists", 10);
        videographer = artistTypeRepository.save(videographer);
        createVideographerFields(videographer);

        // Makeup and Hairdresser
        ArtistType makeupHair = createArtistType("MAKEUP_HAIR", "Makeup & Hairdresser", "Professional Makeup Artists and Hairdressers", 11);
        makeupHair = artistTypeRepository.save(makeupHair);
        createMakeupHairFields(makeupHair);

        // Stand-up Comedian
        ArtistType comedian = createArtistType("COMEDIAN", "Stand-up Comedian", "Stand-up Comedians and Comedy Artists", 12);
        comedian = artistTypeRepository.save(comedian);
        createComedianFields(comedian);

        // Choreographer
        ArtistType choreographer = createArtistType("CHOREOGRAPHER", "Choreographer", "Professional Choreographers", 13);
        choreographer = artistTypeRepository.save(choreographer);
        createChoreographerFields(choreographer);

        // Music Producer
        ArtistType musicProducer = createArtistType("MUSIC_PRODUCER", "Music Producer", "Music Producers and Sound Engineers", 14);
        musicProducer = artistTypeRepository.save(musicProducer);
        createMusicProducerFields(musicProducer);

        // Stunt Performer
        ArtistType stuntPerformer = createArtistType("STUNT_PERFORMER", "Stunt Performer", "Professional Stunt Performers", 15);
        stuntPerformer = artistTypeRepository.save(stuntPerformer);
        createStuntPerformerFields(stuntPerformer);

        // Voice Artist
        ArtistType voiceArtist = createArtistType("VOICE_ARTIST", "Voice Artist", "Voice Artists and Dubbing Artists", 16);
        voiceArtist = artistTypeRepository.save(voiceArtist);
        createVoiceArtistFields(voiceArtist);

        // Comedy Artist
        ArtistType comedyArtist = createArtistType("COMEDY_ARTIST", "Comedy Artist", "Comedy Artists and Humorists", 17);
        comedyArtist = artistTypeRepository.save(comedyArtist);
        createComedyArtistFields(comedyArtist);

        // Event Manager
        ArtistType eventManager = createArtistType("EVENT_MANAGER", "Event Manager", "Event Managers and Event Planners", 18);
        eventManager = artistTypeRepository.save(eventManager);
        createEventManagerFields(eventManager);

        // Costume Designer
        ArtistType costumeDesigner = createArtistType("COSTUME_DESIGNER", "Costume Designer", "Costume Designers and Fashion Stylists", 19);
        costumeDesigner = artistTypeRepository.save(costumeDesigner);
        createCostumeDesignerFields(costumeDesigner);

        // Set Designer
        ArtistType setDesigner = createArtistType("SET_DESIGNER", "Set Designer", "Set Designers and Production Designers", 20);
        setDesigner = artistTypeRepository.save(setDesigner);
        createSetDesignerFields(setDesigner);

        // Light Designer
        ArtistType lightDesigner = createArtistType("LIGHT_DESIGNER", "Light Designer", "Light Designers and Lighting Technicians", 21);
        lightDesigner = artistTypeRepository.save(lightDesigner);
        createLightDesignerFields(lightDesigner);

        // Sound Designer
        ArtistType soundDesigner = createArtistType("SOUND_DESIGNER", "Sound Designer", "Sound Designers and Audio Engineers", 22);
        soundDesigner = artistTypeRepository.save(soundDesigner);
        createSoundDesignerFields(soundDesigner);

        // Editor
        ArtistType editor = createArtistType("EDITOR", "Editor", "Video Editors and Post-Production Specialists", 23);
        editor = artistTypeRepository.save(editor);
        createEditorFields(editor);

        // Animator
        ArtistType animator = createArtistType("ANIMATOR", "Animator", "Animators and Motion Graphics Artists", 24);
        animator = artistTypeRepository.save(animator);
        createAnimatorFields(animator);

        // VFX Artist
        ArtistType vfxArtist = createArtistType("VFX_ARTIST", "VFX Artist", "Visual Effects Artists and Compositors", 25);
        vfxArtist = artistTypeRepository.save(vfxArtist);
        createVfxArtistFields(vfxArtist);

        // Graphic Designer
        ArtistType graphicDesigner = createArtistType("GRAPHIC_DESIGNER", "Graphic Designer", "Graphic Designers and Visual Artists", 26);
        graphicDesigner = artistTypeRepository.save(graphicDesigner);
        createGraphicDesignerFields(graphicDesigner);

        // Art Director
        ArtistType artDirector = createArtistType("ART_DIRECTOR", "Art Director", "Art Directors and Creative Directors", 27);
        artDirector = artistTypeRepository.save(artDirector);
        createArtDirectorFields(artDirector);

        // Production Manager
        ArtistType productionManager = createArtistType("PRODUCTION_MANAGER", "Production Manager", "Production Managers and Line Producers", 28);
        productionManager = artistTypeRepository.save(productionManager);
        createProductionManagerFields(productionManager);

        // Assistant Director
        ArtistType assistantDirector = createArtistType("ASSISTANT_DIRECTOR", "Assistant Director", "Assistant Directors and ADs", 29);
        assistantDirector = artistTypeRepository.save(assistantDirector);
        createAssistantDirectorFields(assistantDirector);

        // Script Supervisor
        ArtistType scriptSupervisor = createArtistType("SCRIPT_SUPERVISOR", "Script Supervisor", "Script Supervisors and Continuity", 30);
        scriptSupervisor = artistTypeRepository.save(scriptSupervisor);
        createScriptSupervisorFields(scriptSupervisor);

        // Casting Assistant
        ArtistType castingAssistant = createArtistType("CASTING_ASSISTANT", "Casting Assistant", "Casting Assistants and Casting Coordinators", 31);
        castingAssistant = artistTypeRepository.save(castingAssistant);
        createCastingAssistantFields(castingAssistant);

        // Location Manager
        ArtistType locationManager = createArtistType("LOCATION_MANAGER", "Location Manager", "Location Managers and Location Scouts", 32);
        locationManager = artistTypeRepository.save(locationManager);
        createLocationManagerFields(locationManager);

        // Transportation Coordinator
        ArtistType transportationCoordinator = createArtistType("TRANSPORTATION_COORDINATOR", "Transportation Coordinator", "Transportation Coordinators and Logistics", 33);
        transportationCoordinator = artistTypeRepository.save(transportationCoordinator);
        createTransportationCoordinatorFields(transportationCoordinator);

        // Catering Manager
        ArtistType cateringManager = createArtistType("CATERING_MANAGER", "Catering Manager", "Catering Managers and Food Coordinators", 34);
        cateringManager = artistTypeRepository.save(cateringManager);
        createCateringManagerFields(cateringManager);

        // Security Coordinator
        ArtistType securityCoordinator = createArtistType("SECURITY_COORDINATOR", "Security Coordinator", "Security Coordinators and Safety Managers", 35);
        securityCoordinator = artistTypeRepository.save(securityCoordinator);
        createSecurityCoordinatorFields(securityCoordinator);

        // Public Relations Manager
        ArtistType prManager = createArtistType("PR_MANAGER", "Public Relations Manager", "PR Managers and Marketing Coordinators", 36);
        prManager = artistTypeRepository.save(prManager);
        createPrManagerFields(prManager);

        // Social Media Manager
        ArtistType socialMediaManager = createArtistType("SOCIAL_MEDIA_MANAGER", "Social Media Manager", "Social Media Managers and Content Creators", 37);
        socialMediaManager = artistTypeRepository.save(socialMediaManager);
        createSocialMediaManagerFields(socialMediaManager);

        // Marketing Manager
        ArtistType marketingManager = createArtistType("MARKETING_MANAGER", "Marketing Manager", "Marketing Managers and Brand Managers", 38);
        marketingManager = artistTypeRepository.save(marketingManager);
        createMarketingManagerFields(marketingManager);

        // Sales Manager
        ArtistType salesManager = createArtistType("SALES_MANAGER", "Sales Manager", "Sales Managers and Business Development", 39);
        salesManager = artistTypeRepository.save(salesManager);
        createSalesManagerFields(salesManager);

        // Finance Manager
        ArtistType financeManager = createArtistType("FINANCE_MANAGER", "Finance Manager", "Finance Managers and Accountants", 40);
        financeManager = artistTypeRepository.save(financeManager);
        createFinanceManagerFields(financeManager);

        // Legal Advisor
        ArtistType legalAdvisor = createArtistType("LEGAL_ADVISOR", "Legal Advisor", "Legal Advisors and Entertainment Lawyers", 41);
        legalAdvisor = artistTypeRepository.save(legalAdvisor);
        createLegalAdvisorFields(legalAdvisor);

        // Talent Agent
        ArtistType talentAgent = createArtistType("TALENT_AGENT", "Talent Agent", "Talent Agents and Artist Representatives", 42);
        talentAgent = artistTypeRepository.save(talentAgent);
        createTalentAgentFields(talentAgent);

        // Manager
        ArtistType manager = createArtistType("MANAGER", "Manager", "Personal Managers and Artist Managers", 43);
        manager = artistTypeRepository.save(manager);
        createManagerFields(manager);

        // Publicist
        ArtistType publicist = createArtistType("PUBLICIST", "Publicist", "Publicists and Media Relations", 44);
        publicist = artistTypeRepository.save(publicist);
        createPublicistFields(publicist);

        // Coordinator
        ArtistType coordinator = createArtistType("COORDINATOR", "Coordinator", "General Coordinators and Project Managers", 45);
        coordinator = artistTypeRepository.save(coordinator);
        createCoordinatorFields(coordinator);

        // Assistant
        ArtistType assistant = createArtistType("ASSISTANT", "Assistant", "General Assistants and Support Staff", 46);
        assistant = artistTypeRepository.save(assistant);
        createAssistantFields(assistant);

        // Intern
        ArtistType intern = createArtistType("INTERN", "Intern", "Interns and Trainees", 47);
        intern = artistTypeRepository.save(intern);
        createInternFields(intern);

        // Volunteer
        ArtistType volunteer = createArtistType("VOLUNTEER", "Volunteer", "Volunteers and Support Staff", 48);
        volunteer = artistTypeRepository.save(volunteer);
        createVolunteerFields(volunteer);

        // Other
        ArtistType other = createArtistType("OTHER", "Other", "Other Creative and Technical Roles", 49);
        other = artistTypeRepository.save(other);
        createOtherFields(other);
    }

    private ArtistType createArtistType(String name, String displayName, String description, int sortOrder) {
        ArtistType artistType = new ArtistType();
        artistType.setName(name);
        artistType.setDisplayName(displayName);
        artistType.setDescription(description);
        artistType.setSortOrder(sortOrder);
        artistType.setIsActive(true);
        return artistType;
    }

    private void createActorFields(ArtistType actor) {
        List<ArtistTypeField> fields = Arrays.asList(
            // Basic physical attributes
            createField(actor, "height", "Height", FieldType.TEXT, true, true, 1, "cm", "Height in centimeters"),
            createField(actor, "weight", "Weight", FieldType.TEXT, true, true, 2, "kg", "Weight in kilograms"),
            createField(actor, "hair_color", "Hair Color", FieldType.SELECT, true, true, 3, null, "Natural hair color"),
            createField(actor, "hair_length", "Hair Length", FieldType.SELECT, true, true, 4, null, "Current hair length"),
            createField(actor, "has_tattoo", "Has Tattoo", FieldType.BOOLEAN, true, true, 5, null, "Do you have any tattoos?"),
            createField(actor, "has_mole", "Has Mole", FieldType.BOOLEAN, true, true, 6, null, "Do you have any moles?"),
            createField(actor, "shoe_size", "Shoe Size", FieldType.TEXT, true, true, 7, null, "Your shoe size"),
            
            // Profile pictures
            createField(actor, "profile_pictures", "Profile Pictures", FieldType.FILE, true, false, 8, null, "3 profile pictures (left, right, front)"),
            
            // Documents
            createField(actor, "passport", "Passport", FieldType.FILE, true, false, 9, null, "Passport document"),
            createField(actor, "aadhar", "Aadhar Card", FieldType.FILE, true, false, 10, null, "Aadhar card document"),
            createField(actor, "pan", "PAN Card", FieldType.FILE, true, false, 11, null, "PAN card document"),
            createField(actor, "id_size_pic", "ID Size Picture", FieldType.FILE, true, false, 12, null, "ID size photograph"),
            
            // Acting specific
            createField(actor, "comfortable_areas", "Comfortable Areas", FieldType.MULTI_SELECT, true, true, 13, null, "Types of shoots you're comfortable with"),
            createField(actor, "acting_videos", "Acting Videos", FieldType.FILE, true, false, 14, null, "9 acting videos (9 ras)"),
            createField(actor, "projects_worked", "Projects Worked", FieldType.TEXTAREA, true, true, 15, null, "List of projects you've worked on with URLs"),
            createField(actor, "travel_cities", "Travel Cities", FieldType.MULTI_SELECT, true, true, 16, null, "Cities where you can travel for shoots"),
            
            // Languages and experience
            createField(actor, "languages_spoken", "Languages Spoken", FieldType.MULTI_SELECT, true, true, 17, null, "Languages you can perform in"),
            createField(actor, "years_experience", "Years of Experience", FieldType.NUMBER, true, true, 18, null, "Years of acting experience"),
            
            // Additional fields
            createField(actor, "special_skills", "Special Skills", FieldType.MULTI_SELECT, false, true, 19, null, "Special acting skills (dancing, singing, etc.)"),
            createField(actor, "demo_reel", "Demo Reel", FieldType.URL, false, false, 20, "https://", "Link to your demo reel"),
            createField(actor, "headshots", "Headshots", FieldType.FILE, false, false, 21, null, "Professional headshot photos"),
            createField(actor, "resume", "Acting Resume", FieldType.FILE, false, false, 22, null, "Your acting resume/CV")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createDancerFields(ArtistType dancer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(dancer, "dance_styles", "Dance Styles", FieldType.MULTI_SELECT, true, true, 1, null, "Dance styles you specialize in"),
            createField(dancer, "training_background", "Training Background", FieldType.TEXTAREA, true, true, 2, null, "Your dance training and education"),
            createField(dancer, "performance_experience", "Performance Experience", FieldType.SELECT, true, true, 3, null, "Years of performance experience"),
            createField(dancer, "choreography_skills", "Choreography Skills", FieldType.BOOLEAN, false, true, 4, null, "Can you create choreography?"),
            createField(dancer, "teaching_experience", "Teaching Experience", FieldType.BOOLEAN, false, true, 5, null, "Do you have teaching experience?"),
            createField(dancer, "performance_videos", "Performance Videos", FieldType.FILE, true, false, 6, null, "Videos of your performances"),
            createField(dancer, "costume_availability", "Costume Availability", FieldType.BOOLEAN, false, false, 7, null, "Do you have your own costumes?"),
            createField(dancer, "flexibility_level", "Flexibility Level", FieldType.SELECT, false, true, 8, null, "What is your current flexibility level?"),
            createField(dancer, "performance_types", "Performance Types", FieldType.MULTI_SELECT, false, true, 9, null, "What types of performances have you done?"),
            createField(dancer, "awards_recognition", "Awards & Recognition", FieldType.TEXTAREA, false, false, 10, null, "Awards and recognitions received"),
            createField(dancer, "availability", "Availability", FieldType.SELECT, false, true, 11, null, "What is your general availability?"),
            createField(dancer, "travel_willingness", "Travel Willingness", FieldType.SELECT, false, true, 12, null, "How far are you willing to travel?")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createSingerFields(ArtistType singer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(singer, "vocal_range", "Vocal Range", FieldType.SELECT, true, true, 1, null, "Your vocal range"),
            createField(singer, "music_genres", "Music Genres", FieldType.MULTI_SELECT, true, true, 2, null, "Genres you can perform"),
            createField(singer, "instruments", "Instruments", FieldType.MULTI_SELECT, false, true, 3, null, "Instruments you can play"),
            createField(singer, "recording_experience", "Recording Experience", FieldType.BOOLEAN, false, true, 4, null, "Do you have recording experience?"),
            createField(singer, "live_performance", "Live Performance", FieldType.BOOLEAN, true, true, 5, null, "Can you perform live?"),
            createField(singer, "demo_tracks", "Demo Tracks", FieldType.FILE, true, false, 6, null, "Your demo recordings"),
            createField(singer, "original_compositions", "Original Compositions", FieldType.BOOLEAN, false, true, 7, null, "Do you write original music?")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createDirectorFields(ArtistType director) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(director, "directing_experience", "Directing Experience", FieldType.SELECT, true, true, 1, null, "Years of directing experience"),
            createField(director, "project_types", "Project Types", FieldType.MULTI_SELECT, true, true, 2, null, "Types of projects you direct"),
            createField(director, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 3, null, "Production equipment you own"),
            createField(director, "team_size", "Team Size", FieldType.SELECT, false, true, 4, null, "Size of teams you can manage"),
            createField(director, "portfolio", "Portfolio", FieldType.URL, true, false, 5, "https://", "Link to your directing portfolio"),
            createField(director, "awards", "Awards", FieldType.TEXTAREA, false, false, 6, null, "Awards and recognitions received")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createWriterFields(ArtistType writer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(writer, "writing_experience", "Writing Experience", FieldType.SELECT, true, true, 1, null, "Years of writing experience"),
            createField(writer, "writing_types", "Writing Types", FieldType.MULTI_SELECT, true, true, 2, null, "Types of content you write"),
            createField(writer, "languages", "Languages", FieldType.MULTI_SELECT, true, true, 3, null, "Languages you write in"),
            createField(writer, "published_works", "Published Works", FieldType.TEXTAREA, false, false, 4, null, "List of your published works"),
            createField(writer, "writing_samples", "Writing Samples", FieldType.FILE, true, false, 5, null, "Samples of your writing work")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createDjFields(ArtistType dj) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(dj, "music_genres", "Music Genres", FieldType.MULTI_SELECT, true, true, 1, null, "Genres you can DJ"),
            createField(dj, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 2, null, "DJ equipment you own"),
            createField(dj, "venue_experience", "Venue Experience", FieldType.MULTI_SELECT, true, true, 3, null, "Types of venues you've performed at"),
            createField(dj, "mixing_skills", "Mixing Skills", FieldType.SELECT, true, true, 4, null, "Your mixing skill level"),
            createField(dj, "demo_mix", "Demo Mix", FieldType.FILE, true, false, 5, null, "Your demo mix recording")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createBandFields(ArtistType band) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(band, "band_size", "Band Size", FieldType.SELECT, true, true, 1, null, "Number of band members"),
            createField(band, "music_genres", "Music Genres", FieldType.MULTI_SELECT, true, true, 2, null, "Genres your band performs"),
            createField(band, "instruments", "Instruments", FieldType.MULTI_SELECT, true, true, 3, null, "Instruments in your band"),
            createField(band, "performance_experience", "Performance Experience", FieldType.SELECT, true, true, 4, null, "Years of band performance experience"),
            createField(band, "original_songs", "Original Songs", FieldType.BOOLEAN, false, true, 5, null, "Do you perform original compositions?"),
            createField(band, "demo_tracks", "Demo Tracks", FieldType.FILE, true, false, 6, null, "Your band's demo recordings"),
            createField(band, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 7, null, "Sound equipment your band owns")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createModelFields(ArtistType model) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(model, "height", "Height", FieldType.TEXT, true, true, 1, "cm", "Height in centimeters"),
            createField(model, "weight", "Weight", FieldType.TEXT, false, true, 2, "kg", "Weight in kilograms"),
            createField(model, "body_measurements", "Body Measurements", FieldType.TEXT, false, true, 3, null, "Bust-Waist-Hip measurements"),
            createField(model, "hair_color", "Hair Color", FieldType.SELECT, false, true, 4, null, "Current hair color"),
            createField(model, "eye_color", "Eye Color", FieldType.SELECT, false, true, 5, null, "Eye color"),
            createField(model, "modeling_types", "Modeling Types", FieldType.MULTI_SELECT, true, true, 6, null, "Types of modeling you do"),
            createField(model, "portfolio", "Portfolio", FieldType.URL, true, false, 7, "https://", "Link to your modeling portfolio"),
            createField(model, "comp_cards", "Comp Cards", FieldType.FILE, false, false, 8, null, "Your composite cards")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createPhotographerFields(ArtistType photographer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(photographer, "photography_types", "Photography Types", FieldType.MULTI_SELECT, true, true, 1, null, "Types of photography you specialize in"),
            createField(photographer, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 2, null, "Photography equipment you own"),
            createField(photographer, "editing_software", "Editing Software", FieldType.MULTI_SELECT, false, true, 3, null, "Photo editing software you use"),
            createField(photographer, "portfolio", "Portfolio", FieldType.URL, true, false, 4, "https://", "Link to your photography portfolio"),
            createField(photographer, "studio_available", "Studio Available", FieldType.BOOLEAN, false, true, 5, null, "Do you have access to a studio?")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createVideographerFields(ArtistType videographer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(videographer, "video_types", "Video Types", FieldType.MULTI_SELECT, true, true, 1, null, "Types of videos you create"),
            createField(videographer, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 2, null, "Video equipment you own"),
            createField(videographer, "editing_software", "Editing Software", FieldType.MULTI_SELECT, false, true, 3, null, "Video editing software you use"),
            createField(videographer, "portfolio", "Portfolio", FieldType.URL, true, false, 4, "https://", "Link to your video portfolio"),
            createField(videographer, "drone_license", "Drone License", FieldType.BOOLEAN, false, true, 5, null, "Do you have a drone pilot license?")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    // Additional field creation methods for new artist types
    private void createMakeupHairFields(ArtistType makeupHair) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(makeupHair, "specialization", "Specialization", FieldType.MULTI_SELECT, true, true, 1, null, "Makeup and hair specializations"),
            createField(makeupHair, "experience_years", "Experience Years", FieldType.SELECT, true, true, 2, null, "Years of experience"),
            createField(makeupHair, "portfolio", "Portfolio", FieldType.URL, true, false, 3, "https://", "Link to your portfolio"),
            createField(makeupHair, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 4, null, "Makeup and hair equipment you own"),
            createField(makeupHair, "certifications", "Certifications", FieldType.TEXTAREA, false, false, 5, null, "Professional certifications")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createComedianFields(ArtistType comedian) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(comedian, "comedy_style", "Comedy Style", FieldType.MULTI_SELECT, true, true, 1, null, "Types of comedy you perform"),
            createField(comedian, "performance_experience", "Performance Experience", FieldType.SELECT, true, true, 2, null, "Years of performance experience"),
            createField(comedian, "demo_videos", "Demo Videos", FieldType.FILE, true, false, 3, null, "Videos of your performances"),
            createField(comedian, "venue_experience", "Venue Experience", FieldType.MULTI_SELECT, false, true, 4, null, "Types of venues you've performed at"),
            createField(comedian, "original_material", "Original Material", FieldType.BOOLEAN, false, true, 5, null, "Do you write original comedy material?")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createChoreographerFields(ArtistType choreographer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(choreographer, "dance_styles", "Dance Styles", FieldType.MULTI_SELECT, true, true, 1, null, "Dance styles you choreograph"),
            createField(choreographer, "choreography_experience", "Choreography Experience", FieldType.SELECT, true, true, 2, null, "Years of choreography experience"),
            createField(choreographer, "performance_videos", "Performance Videos", FieldType.FILE, true, false, 3, null, "Videos of your choreography"),
            createField(choreographer, "teaching_experience", "Teaching Experience", FieldType.BOOLEAN, false, true, 4, null, "Do you teach choreography?"),
            createField(choreographer, "awards", "Awards", FieldType.TEXTAREA, false, false, 5, null, "Awards and recognitions")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createMusicProducerFields(ArtistType musicProducer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(musicProducer, "music_genres", "Music Genres", FieldType.MULTI_SELECT, true, true, 1, null, "Genres you produce"),
            createField(musicProducer, "production_experience", "Production Experience", FieldType.SELECT, true, true, 2, null, "Years of production experience"),
            createField(musicProducer, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 3, null, "Production equipment you own"),
            createField(musicProducer, "portfolio", "Portfolio", FieldType.URL, true, false, 4, "https://", "Link to your portfolio"),
            createField(musicProducer, "software_skills", "Software Skills", FieldType.MULTI_SELECT, false, true, 5, null, "Music production software you use")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createStuntPerformerFields(ArtistType stuntPerformer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(stuntPerformer, "stunt_specialties", "Stunt Specialties", FieldType.MULTI_SELECT, true, true, 1, null, "Types of stunts you perform"),
            createField(stuntPerformer, "safety_certifications", "Safety Certifications", FieldType.MULTI_SELECT, true, true, 2, null, "Safety certifications you hold"),
            createField(stuntPerformer, "demo_reel", "Demo Reel", FieldType.FILE, true, false, 3, null, "Your stunt demo reel"),
            createField(stuntPerformer, "insurance_coverage", "Insurance Coverage", FieldType.BOOLEAN, true, true, 4, null, "Do you have stunt insurance?"),
            createField(stuntPerformer, "physical_condition", "Physical Condition", FieldType.SELECT, true, true, 5, null, "Your current physical condition")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createVoiceArtistFields(ArtistType voiceArtist) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(voiceArtist, "voice_range", "Voice Range", FieldType.SELECT, true, true, 1, null, "Your vocal range"),
            createField(voiceArtist, "languages", "Languages", FieldType.MULTI_SELECT, true, true, 2, null, "Languages you can perform in"),
            createField(voiceArtist, "voice_demo", "Voice Demo", FieldType.FILE, true, false, 3, null, "Your voice demo recording"),
            createField(voiceArtist, "dubbing_experience", "Dubbing Experience", FieldType.BOOLEAN, false, true, 4, null, "Do you have dubbing experience?"),
            createField(voiceArtist, "character_types", "Character Types", FieldType.MULTI_SELECT, false, true, 5, null, "Types of characters you can voice")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createComedyArtistFields(ArtistType comedyArtist) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(comedyArtist, "comedy_types", "Comedy Types", FieldType.MULTI_SELECT, true, true, 1, null, "Types of comedy you perform"),
            createField(comedyArtist, "performance_experience", "Performance Experience", FieldType.SELECT, true, true, 2, null, "Years of performance experience"),
            createField(comedyArtist, "demo_videos", "Demo Videos", FieldType.FILE, true, false, 3, null, "Videos of your performances"),
            createField(comedyArtist, "original_content", "Original Content", FieldType.BOOLEAN, false, true, 4, null, "Do you create original comedy content?"),
            createField(comedyArtist, "social_media_followers", "Social Media Followers", FieldType.NUMBER, false, true, 5, null, "Number of social media followers")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    // Add more field creation methods for other artist types...
    private void createEventManagerFields(ArtistType eventManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(eventManager, "event_types", "Event Types", FieldType.MULTI_SELECT, true, true, 1, null, "Types of events you manage"),
            createField(eventManager, "management_experience", "Management Experience", FieldType.SELECT, true, true, 2, null, "Years of event management experience"),
            createField(eventManager, "portfolio", "Portfolio", FieldType.URL, true, false, 3, "https://", "Link to your portfolio"),
            createField(eventManager, "team_size", "Team Size", FieldType.SELECT, false, true, 4, null, "Size of teams you can manage"),
            createField(eventManager, "budget_range", "Budget Range", FieldType.SELECT, false, true, 5, null, "Budget ranges you can work with")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    // Add placeholder methods for all other artist types
    private void createCostumeDesignerFields(ArtistType costumeDesigner) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(costumeDesigner, "design_specialties", "Design Specialties", FieldType.MULTI_SELECT, true, true, 1, null, "Costume design specialties"),
            createField(costumeDesigner, "portfolio", "Portfolio", FieldType.URL, true, false, 2, "https://", "Link to your portfolio")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createSetDesignerFields(ArtistType setDesigner) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(setDesigner, "design_specialties", "Design Specialties", FieldType.MULTI_SELECT, true, true, 1, null, "Set design specialties"),
            createField(setDesigner, "portfolio", "Portfolio", FieldType.URL, true, false, 2, "https://", "Link to your portfolio")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createLightDesignerFields(ArtistType lightDesigner) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(lightDesigner, "lighting_types", "Lighting Types", FieldType.MULTI_SELECT, true, true, 1, null, "Types of lighting you design"),
            createField(lightDesigner, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 2, null, "Lighting equipment you own")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createSoundDesignerFields(ArtistType soundDesigner) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(soundDesigner, "sound_specialties", "Sound Specialties", FieldType.MULTI_SELECT, true, true, 1, null, "Sound design specialties"),
            createField(soundDesigner, "equipment_owned", "Equipment Owned", FieldType.MULTI_SELECT, false, true, 2, null, "Audio equipment you own")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createEditorFields(ArtistType editor) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(editor, "editing_software", "Editing Software", FieldType.MULTI_SELECT, true, true, 1, null, "Video editing software you use"),
            createField(editor, "editing_experience", "Editing Experience", FieldType.SELECT, true, true, 2, null, "Years of editing experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createAnimatorFields(ArtistType animator) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(animator, "animation_types", "Animation Types", FieldType.MULTI_SELECT, true, true, 1, null, "Types of animation you create"),
            createField(animator, "software_skills", "Software Skills", FieldType.MULTI_SELECT, true, true, 2, null, "Animation software you use")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createVfxArtistFields(ArtistType vfxArtist) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(vfxArtist, "vfx_specialties", "VFX Specialties", FieldType.MULTI_SELECT, true, true, 1, null, "VFX specialties"),
            createField(vfxArtist, "software_skills", "Software Skills", FieldType.MULTI_SELECT, true, true, 2, null, "VFX software you use")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createGraphicDesignerFields(ArtistType graphicDesigner) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(graphicDesigner, "design_specialties", "Design Specialties", FieldType.MULTI_SELECT, true, true, 1, null, "Graphic design specialties"),
            createField(graphicDesigner, "software_skills", "Software Skills", FieldType.MULTI_SELECT, true, true, 2, null, "Design software you use")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createArtDirectorFields(ArtistType artDirector) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(artDirector, "directorial_experience", "Directorial Experience", FieldType.SELECT, true, true, 1, null, "Years of art direction experience"),
            createField(artDirector, "project_types", "Project Types", FieldType.MULTI_SELECT, true, true, 2, null, "Types of projects you direct")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    // Add placeholder methods for remaining artist types
    private void createProductionManagerFields(ArtistType productionManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(productionManager, "management_experience", "Management Experience", FieldType.SELECT, true, true, 1, null, "Years of production management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createAssistantDirectorFields(ArtistType assistantDirector) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(assistantDirector, "ad_experience", "AD Experience", FieldType.SELECT, true, true, 1, null, "Years of assistant directing experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createScriptSupervisorFields(ArtistType scriptSupervisor) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(scriptSupervisor, "supervision_experience", "Supervision Experience", FieldType.SELECT, true, true, 1, null, "Years of script supervision experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createCastingAssistantFields(ArtistType castingAssistant) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(castingAssistant, "casting_experience", "Casting Experience", FieldType.SELECT, true, true, 1, null, "Years of casting experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createLocationManagerFields(ArtistType locationManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(locationManager, "location_experience", "Location Experience", FieldType.SELECT, true, true, 1, null, "Years of location management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createTransportationCoordinatorFields(ArtistType transportationCoordinator) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(transportationCoordinator, "coordination_experience", "Coordination Experience", FieldType.SELECT, true, true, 1, null, "Years of transportation coordination experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createCateringManagerFields(ArtistType cateringManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(cateringManager, "catering_experience", "Catering Experience", FieldType.SELECT, true, true, 1, null, "Years of catering management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createSecurityCoordinatorFields(ArtistType securityCoordinator) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(securityCoordinator, "security_experience", "Security Experience", FieldType.SELECT, true, true, 1, null, "Years of security coordination experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createPrManagerFields(ArtistType prManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(prManager, "pr_experience", "PR Experience", FieldType.SELECT, true, true, 1, null, "Years of PR management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createSocialMediaManagerFields(ArtistType socialMediaManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(socialMediaManager, "social_media_experience", "Social Media Experience", FieldType.SELECT, true, true, 1, null, "Years of social media management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createMarketingManagerFields(ArtistType marketingManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(marketingManager, "marketing_experience", "Marketing Experience", FieldType.SELECT, true, true, 1, null, "Years of marketing management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createSalesManagerFields(ArtistType salesManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(salesManager, "sales_experience", "Sales Experience", FieldType.SELECT, true, true, 1, null, "Years of sales management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createFinanceManagerFields(ArtistType financeManager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(financeManager, "finance_experience", "Finance Experience", FieldType.SELECT, true, true, 1, null, "Years of finance management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createLegalAdvisorFields(ArtistType legalAdvisor) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(legalAdvisor, "legal_experience", "Legal Experience", FieldType.SELECT, true, true, 1, null, "Years of legal advisory experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createTalentAgentFields(ArtistType talentAgent) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(talentAgent, "agent_experience", "Agent Experience", FieldType.SELECT, true, true, 1, null, "Years of talent agency experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createManagerFields(ArtistType manager) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(manager, "management_experience", "Management Experience", FieldType.SELECT, true, true, 1, null, "Years of management experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createPublicistFields(ArtistType publicist) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(publicist, "publicist_experience", "Publicist Experience", FieldType.SELECT, true, true, 1, null, "Years of publicist experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createCoordinatorFields(ArtistType coordinator) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(coordinator, "coordination_experience", "Coordination Experience", FieldType.SELECT, true, true, 1, null, "Years of coordination experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createAssistantFields(ArtistType assistant) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(assistant, "assistant_experience", "Assistant Experience", FieldType.SELECT, true, true, 1, null, "Years of assistant experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createInternFields(ArtistType intern) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(intern, "internship_duration", "Internship Duration", FieldType.SELECT, true, true, 1, null, "Duration of internship")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createVolunteerFields(ArtistType volunteer) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(volunteer, "volunteer_experience", "Volunteer Experience", FieldType.SELECT, true, true, 1, null, "Years of volunteer experience")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private void createOtherFields(ArtistType other) {
        List<ArtistTypeField> fields = Arrays.asList(
            createField(other, "role_description", "Role Description", FieldType.TEXTAREA, true, true, 1, null, "Describe your role and responsibilities")
        );
        artistTypeFieldRepository.saveAll(fields);
    }

    private ArtistTypeField createField(ArtistType artistType, String fieldName, String displayName, 
                                       FieldType fieldType, boolean isRequired, boolean isSearchable, 
                                       int sortOrder, String placeholder, String helpText) {
        ArtistTypeField field = new ArtistTypeField();
        field.setArtistType(artistType);
        field.setFieldName(fieldName);
        field.setDisplayName(displayName);
        field.setFieldType(fieldType);
        field.setIsRequired(isRequired);
        field.setIsSearchable(isSearchable);
        field.setSortOrder(sortOrder);
        field.setPlaceholder(placeholder);
        field.setHelpText(helpText);
        field.setIsActive(true);
        return field;
    }
}
