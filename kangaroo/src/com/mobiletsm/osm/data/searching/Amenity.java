package com.mobiletsm.osm.data.searching;

import java.util.HashMap;
import java.util.Map;

public class Amenity {

	private static Map<String, Integer> amenityMap = new HashMap<String, Integer>();	
	
	public static final String RESTAURANT = "restaurant";
	public static final String PUB = "pub";
	public static final String FOOD_COURT = "food_court";
	public static final String FAST_FOOD = "fast_food";
	public static final String DRINKING_WATER = "drinking_water";
	public static final String BBQ = "bbq";
	public static final String BIERGARTEN = "biergarten";
	public static final String CAFE = "cafe";
	public static final String KINDERGARTEN = "kindergarten";
	public static final String SCHOOL = "school";
	public static final String COLLEGE = "college";
	public static final String LIBRARY = "library";
	public static final String UNIVERSITY = "university";
	public static final String FERRY_TERMINAL = "ferry_terminal";
	public static final String BICYCLE_PARKING = "bicycle_parking";
	public static final String BICYCLE_RENTAL = "bicycle_rental";
	public static final String BUS_STATION = "bus_station";
	public static final String CAR_RENTAL = "car_rental";
	public static final String CAR_SHARING = "car_sharing";
	public static final String CAR_WASH = "car_wash";
	public static final String FUEL = "fuel";
	public static final String GRIT_BIN = "grit_bin";
	public static final String PARKING = "parking";
	public static final String TAXI = "taxi";
	public static final String ATM = "atm";
	public static final String BANK = "bank";
	public static final String BUREAU_DE_CHANGE = "bureau_de_change";
	public static final String PHARMACY = "pharmacy";
	public static final String HOSPITAL = "hospital";
	public static final String BABY_HATCH = "baby_hatch";
	public static final String DENTIST = "dentist";
	public static final String DOCTORS = "doctors";
	public static final String VETERINARY = "veterinary";
	public static final String ARCHITECT_OFFICE = "architect_office";
	public static final String ARTS_CENTRE = "arts_centre";
	public static final String CINEMA = "cinema";
	public static final String FOUNTAIN = "fountain";
	public static final String NIGHTCLUB = "nightclub";
	public static final String STRIPCLUB = "stripclub";
	public static final String STUDIO = "studio";
	public static final String THEATRE = "theatre";
	public static final String BENCH = "bench";
	public static final String BROTHEL = "brothel";
	public static final String CLOCK = "clock";
	public static final String COURTHOUSE = "courthouse";
	public static final String CREMATORIUM = "crematorium";
	public static final String EMBASSY = "embassy";
	public static final String EMERGENCY_PHONE = "emergency_phone";
	public static final String FIRE_HYDRANT = "fire_hydrant";
	public static final String FIRE_STATION = "fire_station";
	public static final String GRAVE_YARD = "grave_yard";
	public static final String HUNTING_STAND = "hunting_stand";
	public static final String MARKETPLACE = "marketplace";
	public static final String PLACE_OF_WORSHIP = "place_of_worship";
	public static final String POLICE = "police";
	public static final String POST_BOX = "post_box";
	public static final String POST_OFFICE = "post_office";
	public static final String PRISON = "prison";
	public static final String PUBLIC_BUILDING = "public_building";
	public static final String RECYCLING = "recycling";
	public static final String SAUNA = "sauna";
	public static final String SHELTER = "shelter";
	public static final String TELEPHONE = "telephone";
	public static final String TOILETS = "toilets";
	public static final String TOWNHALL = "townhall";
	public static final String VENDING_MACHINE = "vending_machine";
	public static final String WASTE_BASKET = "waste_basket";
	public static final String WASTE_DISPOSAL = "waste_disposal";
	
	
	static {
		amenityMap.put(RESTAURANT, 1);
		amenityMap.put(PUB, 2);
		amenityMap.put(FOOD_COURT, 3);
		amenityMap.put(FAST_FOOD, 4);
		amenityMap.put(DRINKING_WATER, 5);
		amenityMap.put(BBQ, 6);
		amenityMap.put(BIERGARTEN, 7);
		amenityMap.put(CAFE, 8);
		amenityMap.put(KINDERGARTEN, 9);
		amenityMap.put(SCHOOL, 10);
		amenityMap.put(COLLEGE, 11);
		amenityMap.put(LIBRARY, 12);
		amenityMap.put(UNIVERSITY, 13);
		amenityMap.put(FERRY_TERMINAL, 14);
		amenityMap.put(BICYCLE_PARKING, 15);
		amenityMap.put(BICYCLE_RENTAL, 16);
		amenityMap.put(BUS_STATION, 17);
		amenityMap.put(CAR_RENTAL, 18);
		amenityMap.put(CAR_SHARING, 19);
		amenityMap.put(CAR_WASH, 20);
		amenityMap.put(FUEL, 21);
		amenityMap.put(GRIT_BIN, 22);
		amenityMap.put(PARKING, 23);
		amenityMap.put(TAXI, 24);
		amenityMap.put(ATM, 25);
		amenityMap.put(BANK, 26);
		amenityMap.put(BUREAU_DE_CHANGE, 27);
		amenityMap.put(PHARMACY, 28);
		amenityMap.put(HOSPITAL, 29);
		amenityMap.put(BABY_HATCH, 30);
		amenityMap.put(DENTIST, 31);
		amenityMap.put(DOCTORS, 32);
		amenityMap.put(VETERINARY, 33);
		amenityMap.put(ARCHITECT_OFFICE, 34);
		amenityMap.put(ARTS_CENTRE, 35);
		amenityMap.put(CINEMA, 36);
		amenityMap.put(FOUNTAIN, 37);
		amenityMap.put(NIGHTCLUB, 38);
		amenityMap.put(STRIPCLUB, 39);
		amenityMap.put(STUDIO, 40);
		amenityMap.put(THEATRE, 41);
		amenityMap.put(BENCH, 42);
		amenityMap.put(BROTHEL, 43);
		amenityMap.put(CLOCK, 44);
		amenityMap.put(COURTHOUSE, 45);
		amenityMap.put(CREMATORIUM, 46);
		amenityMap.put(EMBASSY, 47);
		amenityMap.put(EMERGENCY_PHONE, 48);
		amenityMap.put(FIRE_HYDRANT, 49);
		amenityMap.put(FIRE_STATION, 50);
		amenityMap.put(GRAVE_YARD, 51);
		amenityMap.put(HUNTING_STAND, 52);
		amenityMap.put(MARKETPLACE, 53);
		amenityMap.put(PLACE_OF_WORSHIP, 54);
		amenityMap.put(POLICE, 55);
		amenityMap.put(POST_BOX, 56);
		amenityMap.put(POST_OFFICE, 57);
		amenityMap.put(PRISON, 58);
		amenityMap.put(PUBLIC_BUILDING, 59);
		amenityMap.put(RECYCLING, 60);
		amenityMap.put(SAUNA, 61);
		amenityMap.put(SHELTER, 62);
		amenityMap.put(TELEPHONE, 63);
		amenityMap.put(TOILETS, 64);
		amenityMap.put(TOWNHALL, 65);
		amenityMap.put(VENDING_MACHINE, 66);
		amenityMap.put(WASTE_BASKET, 67);
		amenityMap.put(WASTE_DISPOSAL, 68);
	}
	
	
	private int id;
	
	
	private String type;
	
	
	public Amenity(String type) {
		super();		
		Integer myId = amenityMap.get(type);
		if (myId != null) {
			this.type = type;
			this.id = myId.intValue();
		} else {
			throw new RuntimeException("Amenity: unknown amenity type");
		}		
	}
	
		
	public int getId() {
		return id;
	}
	
	
	public String getType() {
		return type;
	}
	
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Amenity) {
			Amenity amenity = (Amenity)object;
			return amenity.getId() == id;
		} else {
			return false;
		}
	}
	
}
