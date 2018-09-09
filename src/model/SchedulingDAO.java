package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SchedulingDAO {

    private static String loggedInUser;
    private static ZoneId zLocal;

    // First format is for parsing from DB
    // Second format is for displaying to the users
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm");
    private DateTimeFormatter showFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy kk:mm");

    // ZoneID for dates stored in the database
    private ZoneId zUTC = ZoneId.of("UTC");

    /*
        Customer CRUD Methods
     */
    public int[] createCustomer(Customer customer) {

        String sql = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        int addressId = createAddress(customer.getAddress1(), customer.getAddress2(), customer.getCity(), customer.getCountry(), customer.getZipCode(), customer.getPhone());

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, customer.getCustomerName());
            statement.setInt(2, addressId);
            statement.setInt(3, customer.getActive());
            statement.setTimestamp(4, createOrUpdate);
            statement.setString(5, loggedInUser);
            statement.setTimestamp(6, createOrUpdate);
            statement.setString(7, loggedInUser);

            statement.executeUpdate();

            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return new int[]{rs.getInt(1), addressId};
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);
        }

        return new int[]{-1, -1};
    }

    // Update customer method
    public Boolean updateCustomer(Customer customer) {

        String sql = "UPDATE customer SET customerName = ?, active = ?, lastUpdate = ?, lastUpdateBy = ? WHERE customerId = ?";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        // Need to update Address first
        // Which will update city and country
        // Then update customer
        // Updates the address to the already known address ID
        // Unware of city / county, because those are value pairs that any address can utilize, not just one customer
        Boolean successfulUpdate = updateAddress(customer.getAddressID(), customer.getAddress1(), customer.getAddress2(), customer.getCity(), customer.getCountry(), customer.getZipCode(), customer.getPhone());

        if (successfulUpdate) {

            try {

                conn = MYSQLConnection.getConnection();

                statement = conn.prepareStatement(sql);
                statement.setString(1, customer.getCustomerName());
                statement.setInt(2, customer.getActive());
                statement.setTimestamp(3, createOrUpdate);
                statement.setString(4, loggedInUser);
                statement.setInt(5, customer.getCustomerID());

                statement.executeUpdate();

                return true;

            } catch (Exception ex) {
                ex.printStackTrace();
                return false;

            } finally {
                MYSQLConnection.closeConnection(conn);
                MYSQLConnection.closeResultSet(rs);
                MYSQLConnection.closeStatement(statement);

            }

        } else {
            // Failed in update
            return false;
        }

    }

    public Customer retrieveCustomer(int customerID) {

        String sql = "Select customer.customerId, customer.addressId, customer.customerName, customer.active, address.address, address.address2, address.postalCode, address.phone, city.city, country.country "
                + "FROM customer "
                + "INNER JOIN address ON customer.addressId = address.addressId "
                + "INNER JOIN city ON address.cityId = city.cityId "
                + "INNER JOIN country ON city.countryId = country.countryId "
                + "WHERE customerId =" + customerID;

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                // int customerID, String customerName, String address1, String address2, String city, String zipCode, String country, String phone, int active
                Customer cust = new Customer(rs.getInt("customerId"),
                        rs.getInt("addressId"),
                        rs.getString("customerName"),
                        rs.getString("address"),
                        rs.getString("address2"),
                        rs.getString("city"),
                        rs.getString("postalCode"),
                        rs.getString("country"),
                        rs.getString("phone"),
                        rs.getInt("active"));
                return cust;
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);
        }

        return null;

    }

    public ArrayList<Customer> retrieveAllCustomers() {

        ArrayList<Customer> allCusts = new ArrayList<>();
        String sql = "Select customer.customerId, customer.addressId, customer.customerName, customer.active, address.address, address.address2, address.postalCode, address.phone, city.city, country.country "
                + "FROM customer "
                + "INNER JOIN address ON customer.addressId = address.addressId "
                + "INNER JOIN city ON address.cityId = city.cityId "
                + "INNER JOIN country ON city.countryId = country.countryId";

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                // int customerID, String customerName, String address1, String address2, String city, String zipCode, String country, String phone, int active
                Customer cust = new Customer(rs.getInt("customerId"),
                        rs.getInt("addressId"),
                        rs.getString("customerName"),
                        rs.getString("address"),
                        rs.getString("address2"),
                        rs.getString("city"),
                        rs.getString("postalCode"),
                        rs.getString("country"),
                        rs.getString("phone"),
                        rs.getInt("active"));
                allCusts.add(cust);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return allCusts;

    }

    /*
        Address CRUD Methods
     */
    // Method for creating a Address in the DB
    public int createAddress(String address, String address2, String city, String country, String postalCode, String phone) {

        String sql = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        //Check to see if Country exists
        int countryID = lookupCountryExists(country);
        if (countryID == -1) {
            // Country not found
            countryID = createCountry(country);
        }

        //Check to see if City exists
        int cityID = lookupCityExists(city, countryID);
        if (cityID == -1) {
            // City not found
            cityID = createCity(city, countryID);
        }

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, address);
            statement.setString(2, address2);
            statement.setInt(3, cityID);
            statement.setString(4, postalCode);
            statement.setString(5, phone);
            statement.setTimestamp(6, createOrUpdate);
            statement.setString(7, loggedInUser);
            statement.setTimestamp(8, createOrUpdate);
            statement.setString(9, loggedInUser);

            statement.executeUpdate();

            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return -1;
    }

    // Method for creating a Address in the DB
    public Boolean updateAddress(int addressID, String address, String address2, String city, String country, String postalCode, String phone) {

        String sql = "UPDATE address SET address = ?, address2 = ?, cityId = ?, postalCode = ?, phone = ?, lastUpdate = ?, lastUpdateBy = ? WHERE addressId = ?";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        // To reduce data redudancy, looking for city country pairs, and setting
        // them back to the address. 
        //Check to see if the updated country exists already in DB
        int countryID = lookupCountryExists(country);
        if (countryID == -1) {
            // Country not found
            countryID = createCountry(country);
        }

        // Check to see if the updated city, in the updated country exists already in DB
        int cityID = lookupCityExists(city, countryID);
        if (cityID == -1) {
            // City not found
            cityID = createCity(city, countryID);
        }

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql);
            statement.setString(1, address);
            statement.setString(2, address2);
            statement.setInt(3, cityID);
            statement.setString(4, postalCode);
            statement.setString(5, phone);
            statement.setTimestamp(6, createOrUpdate);
            statement.setString(7, loggedInUser);
            statement.setInt(8, addressID);

            statement.executeUpdate();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return false;

    }

    /*
        City CRUD Methods
     */
    // Method for creating a City in the DB
    // Must already have Country ID
    public int createCity(String city, int countryID) {

        String sql = "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, city);
            statement.setInt(2, countryID);
            statement.setTimestamp(3, createOrUpdate);
            statement.setString(4, loggedInUser);
            statement.setTimestamp(5, createOrUpdate);
            statement.setString(6, loggedInUser);

            statement.executeUpdate();

            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return -1;
    }

    // Method for determing if a country already exists in DB
    // Need to ensure the pair, of a city and a country, not just a city alone
    public int lookupCityExists(String city, int countryID) {

        String sql = "SELECT cityId FROM city WHERE city='" + city + "' AND countryId='" + countryID + "'";
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                return rs.getInt("cityId");
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return -1;
    }

    /*
        Country CRUD Methods
     */
    // Method for creating a Country in the DB
    public int createCountry(String country) {

        String sql = "INSERT INTO country (country, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, country);
            statement.setTimestamp(2, createOrUpdate);
            statement.setString(3, loggedInUser);
            statement.setTimestamp(4, createOrUpdate);
            statement.setString(5, loggedInUser);

            statement.executeUpdate();

            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return -1;
    }

    // Method for determining if a country already exists in DB
    public int lookupCountryExists(String country) {

        String sql = "SELECT countryId FROM country WHERE country='" + country + "'";
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                return rs.getInt("countryId");
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);
        }
        return -1;
    }

    /*
        Appointment CRUD Methods
     */
    // Create an appointment
    public int createAppointment(Appointment appointment) {

        String sql = "INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        // Get a LocalDateTime from the string passed from creating the object
        // Format according to standards needed
        LocalDateTime ldtStart = LocalDateTime.parse(appointment.getStart(), showFormat);
        LocalDateTime ldtEnd = LocalDateTime.parse(appointment.getEnd(), showFormat);

        // Converts date time from local to UTC
        ZonedDateTime zdtStart = ldtStart.atZone(zLocal).withZoneSameInstant(zUTC);
        ZonedDateTime zdtEnd = ldtEnd.atZone(zLocal).withZoneSameInstant(zUTC);

        // Sets the LocalDateTime object to the converted time in UTC
        ldtStart = zdtStart.toLocalDateTime();
        ldtEnd = zdtEnd.toLocalDateTime();

        //Create Timestamp values from Instants to update database
        Timestamp timeStart = Timestamp.valueOf(ldtStart);
        Timestamp timeEnd = Timestamp.valueOf(ldtEnd);

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, appointment.getCustomerID());
            statement.setString(2, appointment.getTitle());
            statement.setString(3, appointment.getDescription());
            statement.setString(4, appointment.getLocation());
            statement.setString(5, appointment.getContact());
            statement.setString(6, "");
            statement.setTimestamp(7, timeStart);
            statement.setTimestamp(8, timeEnd);
            statement.setTimestamp(9, createOrUpdate);
            statement.setString(10, loggedInUser);
            statement.setTimestamp(11, createOrUpdate);
            statement.setString(12, loggedInUser);

            statement.executeUpdate();

            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);
        }
        return -1;
    }

    // Selects all appointments
    public ArrayList<Appointment> retrieveAllAppointments() {

        ArrayList<Appointment> allAppointments = new ArrayList<>();
        String sql = "Select * FROM appointment WHERE createdBy='" + loggedInUser + "'";

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {

                // Get the Start and End date from the database
                String startDate = rs.getString("start");
                String endDate = rs.getString("end");

                // Splice off the end that the parser has issues with, that is unneeded
                startDate = startDate.substring(0, startDate.lastIndexOf(':'));
                endDate = endDate.substring(0, endDate.lastIndexOf(':'));

                // Parse the LocalDateTime object from the String returned from the DB
                LocalDateTime ldtStart = LocalDateTime.parse(startDate, df);//, df
                LocalDateTime ldtEnd = LocalDateTime.parse(endDate, df); //, df

                // Converts from UTC to the local timezone
                ZonedDateTime zdtStart = ldtStart.atZone(zUTC).withZoneSameInstant(zLocal);
                ZonedDateTime zdtEnd = ldtEnd.atZone(zUTC).withZoneSameInstant(zLocal);

                // Sets the LocalDateTime to the correct time zone
                ldtStart = zdtStart.toLocalDateTime();
                ldtEnd = zdtEnd.toLocalDateTime();

                Appointment appointment = new Appointment(rs.getInt("appointmentId"),
                        rs.getInt("customerId"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("location"),
                        rs.getString("contact"),
                        ldtStart.format(showFormat),
                        ldtEnd.format(showFormat));

                allAppointments.add(appointment);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);
        }
        return allAppointments;
    }

    // Updates the appointment with new values
    public Boolean updateAppointment(Appointment appointment) {

        String sql = "UPDATE appointment SET title = ?, description = ?, location = ?, contact = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? WHERE appointmentId = ?";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        // Get the current time, and set it to a Timestamp
        Instant now = Instant.now();
        LocalDateTime fromInstant = LocalDateTime.ofInstant(now, zUTC);
        Timestamp createOrUpdate = Timestamp.valueOf(fromInstant);

        // Get a LocalDateTime from the string passed from creating the object
        // Format according to standards needed
        LocalDateTime ldtStart = LocalDateTime.parse(appointment.getStart(), showFormat);
        LocalDateTime ldtEnd = LocalDateTime.parse(appointment.getEnd(), showFormat);

        // Converts date time from local to UTC
        ZonedDateTime zdtStart = ldtStart.atZone(zLocal).withZoneSameInstant(zUTC);
        ZonedDateTime zdtEnd = ldtEnd.atZone(zLocal).withZoneSameInstant(zUTC);

        // Sets the LocalDateTime object to the converted time in UTC
        ldtStart = zdtStart.toLocalDateTime();
        ldtEnd = zdtEnd.toLocalDateTime();

        //Create Timestamp values from Instants to update database
        Timestamp timeStart = Timestamp.valueOf(ldtStart);
        Timestamp timeEnd = Timestamp.valueOf(ldtEnd);

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql);
            statement.setString(1, appointment.getTitle());
            statement.setString(2, appointment.getDescription());
            statement.setString(3, appointment.getLocation());
            statement.setString(4, appointment.getContact());
            statement.setTimestamp(5, timeStart);
            statement.setTimestamp(6, timeEnd);
            statement.setTimestamp(7, createOrUpdate);
            statement.setString(8, loggedInUser);
            statement.setInt(9, appointment.getAppointmentID());

            statement.executeUpdate();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

    }

    // Deletes appointment
    public Boolean deleteAppointment(Appointment appointment) {
        
        String sql = "DELETE FROM appointment WHERE appointmentId = ?";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, appointment.getAppointmentID());

            statement.executeUpdate();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return false;
    }

    /*
        Reporting Queries
     */
    // Groups the appointments by description to count how many of the same type per month
    public ArrayList<AppointmentsPerMonth> numAppointmentsPerMonth() {

        ArrayList<AppointmentsPerMonth> appointments = new ArrayList<>();

        String sql = "SELECT COUNT(description) AS total, description, extract(month from start) as mon "
                + "FROM appointment "
                + "GROUP BY description, extract(month from start) "
                + "ORDER BY mon";

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                AppointmentsPerMonth appoint = new AppointmentsPerMonth(rs.getInt("total"),
                        rs.getString("description"),
                        rs.getInt("mon"));

                appointments.add(appoint);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return appointments;

    }

    // Gets all consultant schedules
    public ArrayList<ConsultantSchedule> schedulePerConsultant() {

        ArrayList<ConsultantSchedule> schedules = new ArrayList<>();

        String sql = "SELECT appointment.appointmentId, appointment.title, customer.customerName, appointment.start, appointment.end, appointment.createdBy FROM appointment "
                + "INNER JOIN customer ON appointment.customerId = customer.customerId "
                + "ORDER BY createdBy, start";

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {

                // Get the Start and End date from the database
                String startDate = rs.getString("start");
                String endDate = rs.getString("end");

                // Splice off the end that the parser has issues with, that is unneeded
                startDate = startDate.substring(0, startDate.lastIndexOf(':'));
                endDate = endDate.substring(0, endDate.lastIndexOf(':'));

                // Parse the LocalDateTime object from the String returned from the DB
                LocalDateTime ldtStart = LocalDateTime.parse(startDate, df);//, df
                LocalDateTime ldtEnd = LocalDateTime.parse(endDate, df); //, df

                // Converts from UTC to the local timezone
                ZonedDateTime zdtStart = ldtStart.atZone(zUTC).withZoneSameInstant(zLocal);
                ZonedDateTime zdtEnd = ldtEnd.atZone(zUTC).withZoneSameInstant(zLocal);

                // Sets the LocalDateTime to the correct time zone
                ldtStart = zdtStart.toLocalDateTime();
                ldtEnd = zdtEnd.toLocalDateTime();

                ConsultantSchedule sch = new ConsultantSchedule(rs.getInt("appointmentId"),
                        rs.getString("title"),
                        rs.getString("customerName"),
                        ldtStart.format(showFormat),
                        ldtEnd.format(showFormat),
                        rs.getString("createdBy"));

                schedules.add(sch);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);
        }
        return schedules;
    }
    
    // Returns logged in Consultant's schedule
    public ArrayList<ConsultantSchedule> consultantsSchedule() {

        ArrayList<ConsultantSchedule> schedules = new ArrayList<>();

        String sql = "SELECT appointment.appointmentId, appointment.title, customer.customerName, appointment.start, appointment.end, appointment.createdBy FROM appointment "
                + "INNER JOIN customer ON appointment.customerId = customer.customerId "
                + "WHERE appointment.createdBy='" + loggedInUser +"' "
                + "ORDER BY appointment.start ";

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {

                // Get the Start and End date from the database
                String startDate = rs.getString("start");
                String endDate = rs.getString("end");

                // Splice off the end that the parser has issues with, that is unneeded
                startDate = startDate.substring(0, startDate.lastIndexOf(':'));
                endDate = endDate.substring(0, endDate.lastIndexOf(':'));

                // Parse the LocalDateTime object from the String returned from the DB
                LocalDateTime ldtStart = LocalDateTime.parse(startDate, df);//, df
                LocalDateTime ldtEnd = LocalDateTime.parse(endDate, df); //, df

                // Converts from UTC to the local timezone
                ZonedDateTime zdtStart = ldtStart.atZone(zUTC).withZoneSameInstant(zLocal);
                ZonedDateTime zdtEnd = ldtEnd.atZone(zUTC).withZoneSameInstant(zLocal);

                // Sets the LocalDateTime to the correct time zone
                ldtStart = zdtStart.toLocalDateTime();
                ldtEnd = zdtEnd.toLocalDateTime();

                ConsultantSchedule sch = new ConsultantSchedule(rs.getInt("appointmentId"),
                        rs.getString("title"),
                        rs.getString("customerName"),
                        ldtStart.format(showFormat),
                        ldtEnd.format(showFormat),
                        rs.getString("createdBy"));

                schedules.add(sch);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return schedules;

    }

    // Selects the active cusomters per country
    public HashMap<String, ArrayList<String>> customersPerCountry() {

        HashMap<String, ArrayList<String>> customers = new HashMap<>();

        String sql = "Select customer.customerName, country.country "
                + "FROM customer "
                + "INNER JOIN address ON customer.addressId = address.addressId "
                + "INNER JOIN city ON address.cityId = city.cityId "
                + "INNER JOIN country ON city.countryId = country.countryId "
                + "WHERE customer.active = 1 "
                + "ORDER BY country.country";

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {

                String currentCountry = rs.getString("country");
                String currentCustomer = rs.getString("customerName");

                if (customers.containsKey(currentCountry)) {
                    ArrayList<String> currentCusts = customers.get(currentCountry);
                    currentCusts.add(currentCustomer);
                    customers.put(currentCountry, currentCusts);
                } else {
                    ArrayList<String> currentCusts = new ArrayList<>();
                    currentCusts.add(currentCustomer);
                    customers.put(currentCountry, currentCusts);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);
        }

        return customers;

    }

    /*
        User Methods
     */
    public Boolean login(String un, String pw) {

        String sql = "SELECT userName, password FROM user "
                + "WHERE userName='" + un + "' AND password='" + pw + "'";

        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;

        try {

            conn = MYSQLConnection.getConnection();

            statement = conn.createStatement();
            rs = statement.executeQuery(sql);

            if (rs.next()) {
                // Check for case sensitivity
                if(rs.getString("userName").equals(un) && rs.getString("password").equals(pw)){
                    return true;
                }else{
                    return false;
                }
                
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            MYSQLConnection.closeConnection(conn);
            MYSQLConnection.closeResultSet(rs);
            MYSQLConnection.closeStatement(statement);

        }

        return false;
    }

    /*
        Getter / Setter for loggedInUser
     */
    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String aLoggedInUser) {
        loggedInUser = aLoggedInUser;
    }

    public ZoneId getzLocal() {
        return zLocal;
    }

    public void setzLocal(ZoneId azLocal) {
        zLocal = azLocal;
    }

}
