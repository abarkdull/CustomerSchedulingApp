Title: Appointment Scheduler
Purpose of application: The purpose of this application is to provide customers/users an efficient way
to schedule appointments with the use of time zone transfer automation.

Author: Austin Barkdull
Contact info:
    -School: abarkd1@wgu.edu
    -Personal: abarkdull@gmail.com

Application Version: 1.02 (22 Apr. 2021)
IDE Version: IntelliJ IDEA 2020.3.3 Ultimate Edition
JDK Version: Java SE 11.0.10
JavaFX Version: JavaFX SDK 11.0.2
MySQL Connector Driver: mysql-connector-java-8.0.22

Location of Javadoc: Software2Project\Javadocs\index.html

Directions:
    -Customers: Navigate to customers page using the "Go to" buttons in the lower right hand corner.
        -Add: To add a customer click add on the customers page and populate the required data fields with values.
              Click save to add the customer to the database.
        -Update: To update a customer, first select a customer in the table view then click update. Update desired the click save
                 to update the database. Note: Sometimes the country/first level division date is not populated when clicking update.
                 I believe this is due to latency of the database connection. However, this is usually very rare and the data is normally populated
                 after clicking cancel then update again.
        -Delete: To delete a customer, first select a customer in the table view then click delete.

    -Appointments: Navigate to appointments page using "Go to" buttons in the lower right hand corner. Filter by week function filters
                   filters the appointments by the current week from previous Monday to next Sunday.
        -Add: To add an appointment click add on the appointments page and populate the required data fields with values. Start times
              are auto-populated in the start time combo box based on the company's office hours (8:00 AM - 10:00 PM EST) in the users
              local time to ensure appointments can only be scheduled during company office hours. End date is auto-populated upon selection
              of a start date with the same date. The end time combo box is auto-filtered based on the start time and 10:00 PM EST local.
              Click save to save the new customer to database.
        -Update: To update an appointment, first select an appointment in the table view then click update. Update desired values then click save
                 to update the database.
        -Delete: To delete an appointment, first select an appointment in the table view then click delete.

    -Reports: Navigate to reports page using "Go to" buttons in the lower right hand corner. Click on a report to generate the report
              in the text area.
              IMPORTANT: A view was created using mySQL workbench to help generate the appointments by type and month report. The code below
              is how I created the view just in case it is needed for evaluation purposes:

                    CREATE VIEW Report AS
                    SELECT MONTH(Start) as Month, Type, COUNT(Appointment_ID) AS Total FROM appointments
                    GROUP BY Month, Type;

Description of additional report:
    -The additional report displays information regarding any update made to an existing customer and/or appointment during the login
     session. The data displays including appointment/customer ID, a timestamp of the update, and who the update was mad by (i.e. the
     current user). This data is useful for tracking appointment re-schedules and customer record updates.
