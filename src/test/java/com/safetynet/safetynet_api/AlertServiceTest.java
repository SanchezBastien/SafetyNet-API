package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.model.*;
import com.safetynet.safetynet_api.service.AlertService;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlertServiceTest {

    @Mock
    private DataLoaderService dataLoaderService;

    @InjectMocks
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChildrenByAddress_withChildrenAndAdults() throws Exception {
        Person child = new Person();
        child.setFirstName("Alice");
        child.setLastName("Smith");
        child.setAddress("123 Main St");
        child.setCity("City");
        child.setZip("12345");
        child.setPhone("1234567890");
        child.setEmail("alice@email.com");

        MedicalRecord childRecord = new MedicalRecord();
        childRecord.setFirstName("Alice");
        childRecord.setLastName("Smith");
        childRecord.setBirthdate("01/05/2015");

        Person adult = new Person();
        adult.setFirstName("Bob");
        adult.setLastName("Smith");
        adult.setAddress("123 Main St");
        adult.setCity("City");
        adult.setZip("12345");
        adult.setPhone("1234567890");
        adult.setEmail("bob@email.com");

        MedicalRecord adultRecord = new MedicalRecord();
        adultRecord.setFirstName("Bob");
        adultRecord.setLastName("Smith");
        adultRecord.setBirthdate("12/07/1985");

        DataWrapper mockData = new DataWrapper();
        mockData.setPersons(Arrays.asList(child, adult));
        mockData.setMedicalrecords(Arrays.asList(childRecord, adultRecord));

        when(dataLoaderService.loadData()).thenReturn(mockData);

        ChildAlertResponse result = alertService.getChildrenByAddress("123 Main St");

        assertNotNull(result);
        assertEquals(1, result.getChildren().size());
        assertEquals("Alice", result.getChildren().getFirst().getFirstName());
        assertEquals(1, result.getHouseholdMembers().size());
        assertEquals("Bob", result.getHouseholdMembers().getFirst().getFirstName());
    }

    @Test
    void testGetFirestationResponse_withAdultsAndChildren() throws Exception {
        Person adult = new Person();
        adult.setFirstName("John");
        adult.setLastName("Doe");
        adult.setAddress("100 Station Rd");
        adult.setCity("City");
        adult.setZip("12345");
        adult.setPhone("1111111111");
        adult.setEmail("john@example.com");

        MedicalRecord adultRecord = new MedicalRecord();
        adultRecord.setFirstName("John");
        adultRecord.setLastName("Doe");
        adultRecord.setBirthdate("01/01/1980");

        Person child = new Person();
        child.setFirstName("Jane");
        child.setLastName("Doe");
        child.setAddress("100 Station Rd");
        child.setCity("City");
        child.setZip("12345");
        child.setPhone("2222222222");
        child.setEmail("jane@example.com");

        MedicalRecord childRecord = new MedicalRecord();
        childRecord.setFirstName("Jane");
        childRecord.setLastName("Doe");
        childRecord.setBirthdate("10/04/2015");

        Firestation station = new Firestation();
        station.setAddress("100 Station Rd");
        station.setStation(1);

        DataWrapper mockData = new DataWrapper();
        mockData.setPersons(Arrays.asList(adult, child));
        mockData.setMedicalrecords(Arrays.asList(adultRecord, childRecord));
        mockData.setFirestations(Collections.singletonList(station));

        when(dataLoaderService.loadData()).thenReturn(mockData);

        FirestationResponse response = alertService.getFirestationResponse(1);

        assertNotNull(response);
        assertEquals(2, response.getPersons().size());
        assertEquals(1, response.getNumberOfAdults());
        assertEquals(1, response.getNumberOfChildren());
    }

    @Test
    void testGetPhoneNumbersByStation_returnsDistinctPhones() throws Exception {
        Person p1 = new Person();
        p1.setFirstName("Anna");
        p1.setLastName("Lee");
        p1.setAddress("150 Fire St");
        p1.setPhone("111-111-1111");

        Person p2 = new Person();
        p2.setFirstName("Mike");
        p2.setLastName("Chan");
        p2.setAddress("150 Fire St");
        p2.setPhone("222-222-2222");

        Person p3 = new Person();
        p3.setFirstName("Sam");
        p3.setLastName("Kim");
        p3.setAddress("150 Fire St");
        p3.setPhone("111-111-1111");  // duplicate

        Firestation station = new Firestation();
        station.setAddress("150 Fire St");
        station.setStation(3);

        DataWrapper mockData = new DataWrapper();
        mockData.setPersons(Arrays.asList(p1, p2, p3));
        mockData.setFirestations(Collections.singletonList(station));

        when(dataLoaderService.loadData()).thenReturn(mockData);

        List<String> phones = alertService.getPhoneNumbersByStation(3);

        assertNotNull(phones);
        assertEquals(2, phones.size());
        assertTrue(phones.contains("111-111-1111"));
        assertTrue(phones.contains("222-222-2222"));
    }

    @Test
    void testGetResidentsByAddress_returnsCorrectData() throws Exception {
        Person resident = new Person();
        resident.setFirstName("Claire");
        resident.setLastName("Brown");
        resident.setAddress("789 Maple Ave");
        resident.setPhone("333-333-3333");

        MedicalRecord record = new MedicalRecord();
        record.setFirstName("Claire");
        record.setLastName("Brown");
        record.setBirthdate("01/01/1988");
        record.setMedications(List.of("aspirin:100mg"));
        record.setAllergies(List.of("peanut"));

        Firestation station = new Firestation();
        station.setAddress("789 Maple Ave");
        station.setStation(5);

        DataWrapper mockData = new DataWrapper();
        mockData.setPersons(Collections.singletonList(resident));
        mockData.setMedicalrecords(Collections.singletonList(record));
        mockData.setFirestations(Collections.singletonList(station));

        when(dataLoaderService.loadData()).thenReturn(mockData);

        FireAddressResponse response = alertService.getResidentsByAddress("789 Maple Ave");

        assertNotNull(response);
        assertEquals(5, response.getStationNumber());
        assertEquals(1, response.getResidents().size());
        assertEquals("Claire", response.getResidents().getFirst().getFirstName());
        assertEquals("aspirin:100mg", response.getResidents().getFirst().getMedications().getFirst());
        assertEquals("peanut", response.getResidents().getFirst().getAllergies().getFirst());
    }

    @Test
    void testGetFloodData_returnsHouseholdsGroupedByAddress() throws Exception {
        Person p1 = new Person();
        p1.setFirstName("Tom");
        p1.setLastName("Hardy");
        p1.setAddress("22 River St");
        p1.setPhone("555-000-0000");

        MedicalRecord r1 = new MedicalRecord();
        r1.setFirstName("Tom");
        r1.setLastName("Hardy");
        r1.setBirthdate("01/01/1990");
        r1.setMedications(Collections.singletonList("med1"));
        r1.setAllergies(Collections.singletonList("allergy1"));

        Firestation s1 = new Firestation();
        s1.setAddress("22 River St");
        s1.setStation(9);

        DataWrapper mockData = new DataWrapper();
        mockData.setPersons(Collections.singletonList(p1));
        mockData.setMedicalrecords(Collections.singletonList(r1));
        mockData.setFirestations(Collections.singletonList(s1));

        when(dataLoaderService.loadData()).thenReturn(mockData);

        FloodResponse response = alertService.getFloodData(Collections.singletonList(9));

        assertNotNull(response);
        assertEquals(1, response.getHouseholds().size());
        assertTrue(response.getHouseholds().containsKey("22 River St"));
        List<FloodResponse.HouseholdMember> members = response.getHouseholds().get("22 River St");
        assertEquals(1, members.size());
        assertEquals("Tom", members.getFirst().getFirstName());
        assertEquals("med1", members.getFirst().getMedications().getFirst());
        assertEquals("allergy1", members.getFirst().getAllergies().getFirst());
    }
}