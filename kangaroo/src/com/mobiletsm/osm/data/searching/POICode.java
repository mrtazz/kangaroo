package com.mobiletsm.osm.data.searching;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

public class POICode {

	/**
	 * 
	 */
	private static Map<String, Integer> poiCodeMap = new HashMap<String, Integer>();	
	
	
	/* amenities */
	public static final String AMENITY_RESTAURANT = "amenity#restaurant";
	public static final String AMENITY_PUB = "amenity#pub";
	public static final String AMENITY_FOOD_COURT = "amenity#food_court";
	public static final String AMENITY_FAST_FOOD = "amenity#fast_food";
	public static final String AMENITY_DRINKING_WATER = "amenity#drinking_water";
	public static final String AMENITY_BBQ = "amenity#bbq";
	public static final String AMENITY_BIERGARTEN = "amenity#biergarten";
	public static final String AMENITY_CAFE = "amenity#cafe";
	public static final String AMENITY_KINDERGARTEN = "amenity#kindergarten";
	public static final String AMENITY_SCHOOL = "amenity#school";
	public static final String AMENITY_COLLEGE = "amenity#college";
	public static final String AMENITY_LIBRARY = "amenity#library";
	public static final String AMENITY_UNIVERSITY = "amenity#university";
	public static final String AMENITY_FERRY_TERMINAL = "amenity#ferry_terminal";
	public static final String AMENITY_BICYCLE_PARKING = "amenity#bicycle_parking";
	public static final String AMENITY_BICYCLE_RENTAL = "amenity#bicycle_rental";
	public static final String AMENITY_BUS_STATION = "amenity#bus_station";
	public static final String AMENITY_CAR_RENTAL = "amenity#car_rental";
	public static final String AMENITY_CAR_SHARING = "amenity#car_sharing";
	public static final String AMENITY_CAR_WASH = "amenity#car_wash";
	public static final String AMENITY_FUEL = "amenity#fuel";
	public static final String AMENITY_GRIT_BIN = "amenity#grit_bin";
	public static final String AMENITY_PARKING = "amenity#parking";
	public static final String AMENITY_TAXI = "amenity#taxi";
	public static final String AMENITY_ATM = "amenity#atm";
	public static final String AMENITY_BANK = "amenity#bank";
	public static final String AMENITY_BUREAU_DE_CHANGE = "amenity#bureau_de_change";
	public static final String AMENITY_PHARMACY = "amenity#pharmacy";
	public static final String AMENITY_HOSPITAL = "amenity#hospital";
	public static final String AMENITY_BABY_HATCH = "amenity#baby_hatch";
	public static final String AMENITY_DENTIST = "amenity#dentist";
	public static final String AMENITY_DOCTORS = "amenity#doctors";
	public static final String AMENITY_VETERINARY = "amenity#veterinary";
	public static final String AMENITY_ARCHITECT_OFFICE = "amenity#architect_office";
	public static final String AMENITY_ARTS_CENTRE = "amenity#arts_centre";
	public static final String AMENITY_CINEMA = "amenity#cinema";
	public static final String AMENITY_FOUNTAIN = "amenity#fountain";
	public static final String AMENITY_NIGHTCLUB = "amenity#nightclub";
	public static final String AMENITY_STRIPCLUB = "amenity#stripclub";
	public static final String AMENITY_STUDIO = "amenity#studio";
	public static final String AMENITY_THEATRE = "amenity#theatre";
	public static final String AMENITY_BENCH = "amenity#bench";
	public static final String AMENITY_BROTHEL = "amenity#brothel";
	public static final String AMENITY_CLOCK = "amenity#clock";
	public static final String AMENITY_COURTHOUSE = "amenity#courthouse";
	public static final String AMENITY_CREMATORIUM = "amenity#crematorium";
	public static final String AMENITY_EMBASSY = "amenity#embassy";
	public static final String AMENITY_EMERGENCY_PHONE = "amenity#emergency_phone";
	public static final String AMENITY_FIRE_HYDRANT = "amenity#fire_hydrant";
	public static final String AMENITY_FIRE_STATION = "amenity#fire_station";
	public static final String AMENITY_GRAVE_YARD = "amenity#grave_yard";
	public static final String AMENITY_HUNTING_STAND = "amenity#hunting_stand";
	public static final String AMENITY_MARKETPLACE = "amenity#marketplace";
	public static final String AMENITY_PLACE_OF_WORSHIP = "amenity#place_of_worship";
	public static final String AMENITY_POLICE = "amenity#police";
	public static final String AMENITY_POST_BOX = "amenity#post_box";
	public static final String AMENITY_POST_OFFICE = "amenity#post_office";
	public static final String AMENITY_PRISON = "amenity#prison";
	public static final String AMENITY_PUBLIC_BUILDING = "amenity#public_building";
	public static final String AMENITY_RECYCLING = "amenity#recycling";
	public static final String AMENITY_SAUNA = "amenity#sauna";
	public static final String AMENITY_SHELTER = "amenity#shelter";
	public static final String AMENITY_TELEPHONE = "amenity#telephone";
	public static final String AMENITY_TOILETS = "amenity#toilets";
	public static final String AMENITY_TOWNHALL = "amenity#townhall";
	public static final String AMENITY_VENDING_MACHINE = "amenity#vending_machine";
	public static final String AMENITY_WASTE_BASKET = "amenity#waste_basket";
	public static final String AMENITY_WASTE_DISPOSAL = "amenity#waste_disposal";

	/* shops */
	public static final String SHOP_ALCOHOL = "shop#alcohol";
	public static final String SHOP_BAKERY = "shop#bakery";
	public static final String SHOP_BEVERAGES = "shop#beverages";
	public static final String SHOP_BICYCLE = "shop#bicycle";
	public static final String SHOP_BOOKS = "shop#books";
	public static final String SHOP_BUTCHER = "shop#butcher";
	public static final String SHOP_CAR = "shop#car";
	public static final String SHOP_CAR_REPAIR = "shop#car_repair";
	public static final String SHOP_CHEMIST = "shop#chemist";
	public static final String SHOP_CLOTHES = "shop#clothes";
	public static final String SHOP_COMPUTER = "shop#computer";
	public static final String SHOP_CONFECTIONERY = "shop#confectionery";
	public static final String SHOP_CONVENIENCE = "shop#convenience";
	public static final String SHOP_DEPARTMENT_STORE = "shop#department_store";
	public static final String SHOP_DRY_CLEANING = "shop#dry_cleaning";
	public static final String SHOP_DOITYOURSELF = "shop#doityourself";
	public static final String SHOP_ELECTRONICS = "shop#electronics";
	public static final String SHOP_FABRICS = "shop#fabrics";
	public static final String SHOP_FARM = "shop#farm";
	public static final String SHOP_FLORIST = "shop#florist";
	public static final String SHOP_FUNERAL_DIRECTORS = "shop#funeral_directors";
	public static final String SHOP_FURNITURE = "shop#furniture";
	public static final String SHOP_GARDEN_CENTRE = "shop#garden_centre";
	public static final String SHOP_GENERAL = "shop#general";
	public static final String SHOP_GIFT = "shop#gift";
	public static final String SHOP_GLAZIERY = "shop#glaziery";
	public static final String SHOP_GREENGROCER = "shop#greengrocer";
	public static final String SHOP_HAIRDRESSER = "shop#hairdresser";
	public static final String SHOP_HARDWARE = "shop#hardware";
	public static final String SHOP_HEARING_AIDS = "shop#hearing_aids";
	public static final String SHOP_HIFI = "shop#hifi";
	public static final String SHOP_JEWELRY = "shop#jewelry";
	public static final String SHOP_KIOSK = "shop#kiosk";
	public static final String SHOP_LAUNDRY = "shop#laundry";
	public static final String SHOP_MALL = "shop#mall";
	public static final String SHOP_MASSAGE = "shop#massage";
	public static final String SHOP_MOTORCYCLE = "shop#motorcycle";
	public static final String SHOP_NEWSAGENT = "shop#newsagent";
	public static final String SHOP_OPTICIAN = "shop#optician";
	public static final String SHOP_ORGANIC = "shop#organic";
	public static final String SHOP_OUTDOOR = "shop#outdoor";
	public static final String SHOP_SECOND_HAND = "shop#second_hand";
	public static final String SHOP_SPORTS = "shop#sports";
	public static final String SHOP_STATIONERY = "shop#stationery";
	public static final String SHOP_SUPERMARKET = "shop#supermarket";
	public static final String SHOP_SHOES = "shop#shoes";
	public static final String SHOP_TOYS = "shop#toys";
	public static final String SHOP_TRAVEL_AGENCY = "shop#travel_agency";
	public static final String SHOP_VIDEO = "shop#video";

	
	static {
		
		/* amenities */
		poiCodeMap.put(AMENITY_RESTAURANT, 1);
		poiCodeMap.put(AMENITY_PUB, 2);
		poiCodeMap.put(AMENITY_FOOD_COURT, 3);
		poiCodeMap.put(AMENITY_FAST_FOOD, 4);
		poiCodeMap.put(AMENITY_DRINKING_WATER, 5);
		poiCodeMap.put(AMENITY_BBQ, 6);
		poiCodeMap.put(AMENITY_BIERGARTEN, 7);
		poiCodeMap.put(AMENITY_CAFE, 8);
		poiCodeMap.put(AMENITY_KINDERGARTEN, 9);
		poiCodeMap.put(AMENITY_SCHOOL, 10);
		poiCodeMap.put(AMENITY_COLLEGE, 11);
		poiCodeMap.put(AMENITY_LIBRARY, 12);
		poiCodeMap.put(AMENITY_UNIVERSITY, 13);
		poiCodeMap.put(AMENITY_FERRY_TERMINAL, 14);
		poiCodeMap.put(AMENITY_BICYCLE_PARKING, 15);
		poiCodeMap.put(AMENITY_BICYCLE_RENTAL, 16);
		poiCodeMap.put(AMENITY_BUS_STATION, 17);
		poiCodeMap.put(AMENITY_CAR_RENTAL, 18);
		poiCodeMap.put(AMENITY_CAR_SHARING, 19);
		poiCodeMap.put(AMENITY_CAR_WASH, 20);
		poiCodeMap.put(AMENITY_FUEL, 21);
		poiCodeMap.put(AMENITY_GRIT_BIN, 22);
		poiCodeMap.put(AMENITY_PARKING, 23);
		poiCodeMap.put(AMENITY_TAXI, 24);
		poiCodeMap.put(AMENITY_ATM, 25);
		poiCodeMap.put(AMENITY_BANK, 26);
		poiCodeMap.put(AMENITY_BUREAU_DE_CHANGE, 27);
		poiCodeMap.put(AMENITY_PHARMACY, 28);
		poiCodeMap.put(AMENITY_HOSPITAL, 29);
		poiCodeMap.put(AMENITY_BABY_HATCH, 30);
		poiCodeMap.put(AMENITY_DENTIST, 31);
		poiCodeMap.put(AMENITY_DOCTORS, 32);
		poiCodeMap.put(AMENITY_VETERINARY, 33);
		poiCodeMap.put(AMENITY_ARCHITECT_OFFICE, 34);
		poiCodeMap.put(AMENITY_ARTS_CENTRE, 35);
		poiCodeMap.put(AMENITY_CINEMA, 36);
		poiCodeMap.put(AMENITY_FOUNTAIN, 37);
		poiCodeMap.put(AMENITY_NIGHTCLUB, 38);
		poiCodeMap.put(AMENITY_STRIPCLUB, 39);
		poiCodeMap.put(AMENITY_STUDIO, 40);
		poiCodeMap.put(AMENITY_THEATRE, 41);
		poiCodeMap.put(AMENITY_BENCH, 42);
		poiCodeMap.put(AMENITY_BROTHEL, 43);
		poiCodeMap.put(AMENITY_CLOCK, 44);
		poiCodeMap.put(AMENITY_COURTHOUSE, 45);
		poiCodeMap.put(AMENITY_CREMATORIUM, 46);
		poiCodeMap.put(AMENITY_EMBASSY, 47);
		poiCodeMap.put(AMENITY_EMERGENCY_PHONE, 48);
		poiCodeMap.put(AMENITY_FIRE_HYDRANT, 49);
		poiCodeMap.put(AMENITY_FIRE_STATION, 50);
		poiCodeMap.put(AMENITY_GRAVE_YARD, 51);
		poiCodeMap.put(AMENITY_HUNTING_STAND, 52);
		poiCodeMap.put(AMENITY_MARKETPLACE, 53);
		poiCodeMap.put(AMENITY_PLACE_OF_WORSHIP, 54);
		poiCodeMap.put(AMENITY_POLICE, 55);
		poiCodeMap.put(AMENITY_POST_BOX, 56);
		poiCodeMap.put(AMENITY_POST_OFFICE, 57);
		poiCodeMap.put(AMENITY_PRISON, 58);
		poiCodeMap.put(AMENITY_PUBLIC_BUILDING, 59);
		poiCodeMap.put(AMENITY_RECYCLING, 60);
		poiCodeMap.put(AMENITY_SAUNA, 61);
		poiCodeMap.put(AMENITY_SHELTER, 62);
		poiCodeMap.put(AMENITY_TELEPHONE, 63);
		poiCodeMap.put(AMENITY_TOILETS, 64);
		poiCodeMap.put(AMENITY_TOWNHALL, 65);
		poiCodeMap.put(AMENITY_VENDING_MACHINE, 66);
		poiCodeMap.put(AMENITY_WASTE_BASKET, 67);
		poiCodeMap.put(AMENITY_WASTE_DISPOSAL, 68);

		/* shops */
		poiCodeMap.put(SHOP_ALCOHOL, 100);
		poiCodeMap.put(SHOP_BAKERY, 101);
		poiCodeMap.put(SHOP_BEVERAGES, 102);
		poiCodeMap.put(SHOP_BICYCLE, 103);
		poiCodeMap.put(SHOP_BOOKS, 104);
		poiCodeMap.put(SHOP_BUTCHER, 105);
		poiCodeMap.put(SHOP_CAR, 106);
		poiCodeMap.put(SHOP_CAR_REPAIR, 107);
		poiCodeMap.put(SHOP_CHEMIST, 108);
		poiCodeMap.put(SHOP_CLOTHES, 109);
		poiCodeMap.put(SHOP_COMPUTER, 110);
		poiCodeMap.put(SHOP_CONFECTIONERY, 111);
		poiCodeMap.put(SHOP_CONVENIENCE, 112);
		poiCodeMap.put(SHOP_DEPARTMENT_STORE, 113);
		poiCodeMap.put(SHOP_DRY_CLEANING, 114);
		poiCodeMap.put(SHOP_DOITYOURSELF, 115);
		poiCodeMap.put(SHOP_ELECTRONICS, 116);
		poiCodeMap.put(SHOP_FABRICS, 117);
		poiCodeMap.put(SHOP_FARM, 118);
		poiCodeMap.put(SHOP_FLORIST, 119);
		poiCodeMap.put(SHOP_FUNERAL_DIRECTORS, 120);
		poiCodeMap.put(SHOP_FURNITURE, 121);
		poiCodeMap.put(SHOP_GARDEN_CENTRE, 122);
		poiCodeMap.put(SHOP_GENERAL, 123);
		poiCodeMap.put(SHOP_GIFT, 124);
		poiCodeMap.put(SHOP_GLAZIERY, 125);
		poiCodeMap.put(SHOP_GREENGROCER, 126);
		poiCodeMap.put(SHOP_HAIRDRESSER, 127);
		poiCodeMap.put(SHOP_HARDWARE, 128);
		poiCodeMap.put(SHOP_HEARING_AIDS, 129);
		poiCodeMap.put(SHOP_HIFI, 130);
		poiCodeMap.put(SHOP_JEWELRY, 131);
		poiCodeMap.put(SHOP_KIOSK, 132);
		poiCodeMap.put(SHOP_LAUNDRY, 133);
		poiCodeMap.put(SHOP_MALL, 134);
		poiCodeMap.put(SHOP_MASSAGE, 135);
		poiCodeMap.put(SHOP_MOTORCYCLE, 136);
		poiCodeMap.put(SHOP_NEWSAGENT, 137);
		poiCodeMap.put(SHOP_OPTICIAN, 138);
		poiCodeMap.put(SHOP_ORGANIC, 139);
		poiCodeMap.put(SHOP_OUTDOOR, 140);
		poiCodeMap.put(SHOP_SECOND_HAND, 141);
		poiCodeMap.put(SHOP_SPORTS, 142);
		poiCodeMap.put(SHOP_STATIONERY, 143);
		poiCodeMap.put(SHOP_SUPERMARKET, 144);
		poiCodeMap.put(SHOP_SHOES, 145);
		poiCodeMap.put(SHOP_TOYS, 146);
		poiCodeMap.put(SHOP_TRAVEL_AGENCY, 147);
		poiCodeMap.put(SHOP_VIDEO, 148);

	}
	
	
	private int id;
	
	
	private String type;
	
	
	public POICode(String type) {
		super();		
		Integer myId = poiCodeMap.get(type);
		if (myId != null) {
			this.type = type;
			this.id = myId.intValue();
		} else {
			throw new RuntimeException("POINode(): unknown type");
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
		if (object instanceof POICode) {
			POICode poiNode = (POICode)object;
			return poiNode.getId() == id;
		} else {
			return false;
		}
	}
	
	
	public static POICode createFromTags(Collection<Tag> tags) {		
		POICode poiNode = null;
		for (Tag tag : tags) {
			String tagString = getTagString(tag);
			if (poiCodeMap.containsKey(tagString)) {
				if (poiNode == null) {
					poiNode = new POICode(tagString);
				} else {
					System.out.println("POICode.createFromTags(): multiple choices possible");
				}
			}
		}		
		return poiNode;
	}
	
	
	public static String getTagString(Tag tag) {
		return tag.getKey() + "#" + tag.getValue();
	}
	
	
	public static Map<String, Integer> getPOICodeMap() {
		return Collections.unmodifiableMap(poiCodeMap);
	}
	
}
