//Script Log September 2025

CREATE TABLE user_employee (
                               user_id BIGINT NOT NULL,
                               employee_id BIGINT NOT NULL,
                               PRIMARY KEY (user_id, employee_id),
                               CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id)
);

-- TODO: DELETE TABLE user_employee After below queries are executed and changes related to user_employee table are removed



CREATE TABLE employee (
                          id CHAR(36) PRIMARY KEY,
                          num_employee VARCHAR(11),
                          name VARCHAR(50),
                          grouper_1 VARCHAR(50),
                          grouper_2 VARCHAR(50),
                          grouper_3 VARCHAR(50),
                          grouper_4 VARCHAR(50),
                          grouper_5 VARCHAR(50),
                          start_date DATE
);


CREATE TABLE user_groupers (
                               id SERIAL PRIMARY KEY,
                               user_id INT NOT NULL,
                               grouper_1 VARCHAR(50),
                               grouper_2 VARCHAR(50),
                               grouper_3 VARCHAR(50),
                               grouper_4 VARCHAR(50),
                               grouper_5 VARCHAR(50),
                               CONSTRAINT fk_user_groupers_user FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE timesheets (
                            id CHAR(36) PRIMARY KEY,
                            timesheet_identifier CHAR(5) NOT NULL,
                            description VARCHAR(255),
                            days_of_the_week SMALLINT NOT NULL,
                            entry_time TIME,
                            break_departure_time TIME,
                            break_return_time TIME,
                            departure_time TIME
);

CREATE TABLE employee_timesheet (
                                    id CHAR(36) PRIMARY KEY,
                                    employee_id CHAR(36) NOT NULL,
                                    timesheet_id CHAR(36) NOT NULL,
                                    from_date DATE NOT NULL,
                                    to_date DATE NOT NULL,
                                    CONSTRAINT fk_employee_timesheet_employee FOREIGN KEY (employee_id) REFERENCES employee(id),
                                    CONSTRAINT fk_employee_timesheet_timesheet FOREIGN KEY (timesheet_id) REFERENCES timesheets(id)
);


CREATE TABLE festive_days (
                              id SERIAL PRIMARY KEY,
                              day INT NOT NULL,
                              month INT NOT NULL,
                              name VARCHAR(100) NOT NULL,
                              description VARCHAR(255)
);


INSERT INTO festive_days (day, month, name, description) VALUES
                                                             (1, 1, 'Año Nuevo', 'New Year''s Day'),
                                                             (5, 2, 'Día de la Constitución', 'Constitution Day'),
                                                             (21, 3, 'Natalicio de Benito Juárez', 'Benito Juárez''s Birthday'),
                                                             (1, 5, 'Día del Trabajo', 'Labor Day'),
                                                             (16, 9, 'Día de la Independencia', 'Independence Day'),
                                                             (20, 11, 'Día de la Revolución', 'Revolution Day'),
                                                             (25, 12, 'Navidad', 'Christmas Day');


CREATE TABLE groupers_configurations (
                                         id SERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         short_name VARCHAR(50) NOT NULL,
                                         visible BOOLEAN NOT NULL
);


CREATE TABLE time_records (
                              id CHAR(36) PRIMARY KEY,
                              employee_id CHAR(36) NOT NULL,
                              turn INT NOT NULL,
                              entry_time TIME,
                              break_departure_time TIME,
                              break_return_time TIME,
                              departure_time TIME,
                              CONSTRAINT fk_time_records_employee FOREIGN KEY (employee_id) REFERENCES employee(id)
);

CREATE TABLE time_rules (
                       id CHAR(36) PRIMARY KEY,
                       description VARCHAR(255),
                       level INT,
                       sequence INT,
                       rule VARCHAR(255),
                       result_meets VARCHAR(10),
                       exclusive BOOLEAN
);


CREATE TABLE timesheet_time_rule (
                                     timesheet_id CHAR(36) NOT NULL,
                                     time_rule_id CHAR(36) NOT NULL,
                                     PRIMARY KEY (timesheet_id, time_rule_id),
                                     CONSTRAINT fk_timesheet_time_rule_timesheet FOREIGN KEY (timesheet_id) REFERENCES timesheets(id),
                                     CONSTRAINT fk_timesheet_time_rule_time_rule FOREIGN KEY (time_rule_id) REFERENCES time_rules(id)
);

CREATE TABLE periods (
    id SERIAL PRIMARY KEY,
    grouper_1 VARCHAR(50),
    grouper_2 VARCHAR(50),
    grouper_3 VARCHAR(50),
    grouper_4 VARCHAR(50),
    grouper_5 VARCHAR(50),
    from_date DATE NOT NULL,
    to_date DATE NOT NULL
);
