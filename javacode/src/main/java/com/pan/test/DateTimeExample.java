package com.pan.test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

public class DateTimeExample {
	
	public static void main(String[] args) {
		ZonedDateTime zonedDateTime1 = ZonedDateTime.parse("2015-05-03T10:15:30+01:00[Europe/Paris]");
		System.out.println("zonedDateTime1:"+zonedDateTime1);
		System.out.println("zonedDateTime1 instance:"+zonedDateTime1.toInstant());
		
		
		System.out.println("Instant.now():"+Instant.now()); // Instant is the UTC time
		
		
		
		Set<String> allZoneIds = ZoneId.getAvailableZoneIds();
		//System.out.println(allZoneIds);
		LocalDateTime localDateTime = LocalDateTime.now();
		System.out.println("localDateTime:"+localDateTime);
		ZoneId zoneId = ZoneId.of("Hongkong");
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
		System.out.println("zonedDateTime:"+zonedDateTime);
		System.out.println("zoned instance:"+zonedDateTime.toInstant());
		
		
		System.out.println("zonedDateTime to local:"+zonedDateTime.toLocalDateTime());
		
		
		
		System.out.println("LocalDateTime to Instant:"+LocalDateTime.now().toInstant(ZoneOffset.UTC));

	}

}
