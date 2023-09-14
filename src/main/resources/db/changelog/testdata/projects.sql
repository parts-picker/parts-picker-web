-- project DIY Smart Home Automation System
-- Insert an instance into the workflow_instances table
INSERT INTO workflow_instances (id, current_node_id, active, message, display_type)
VALUES
  (nextval('instance_id_seq'),
   (SELECT id FROM workflow_nodes WHERE workflow_id = (SELECT id FROM workflows WHERE name = 'project_workflow' AND version = 0) AND name = 'planning'),
   true,
   NULL,
   'DEFAULT'
  );

-- Insert a sample project into the projects table with an extensive description
INSERT INTO projects (id, name, short_description, group_id, instance_id, description)
VALUES
  (nextval('project_seq'), 'DIY Smart Home Automation System', 'Create a complete home automation system with Arduino and Raspberry Pi.', NULL, currval('instance_id_seq'),
   E'## DIY Smart Home Automation System\n\nWelcome to the world of cutting-edge home automation! ' ||
   E'In this ambitious project, you will embark on a journey to design and build ' ||
   E'a comprehensive smart home automation system using a combination of Arduino and Raspberry Pi. ' ||
   E'Imagine controlling your lights, temperature, security, and entertainment systems ' ||
   E'with just a tap on your smartphone or a voice command. ' ||
   E'This project involves a multitude of components and technologies, ' ||
   E'including sensors, actuators, relays, cameras, and IoT communication protocols. ' ||
   E'You''ll learn how to program Arduino microcontrollers and Raspberry Pi, ' ||
   E'integrate sensors to detect motion, light, and temperature, ' ||
   E'and create a user-friendly web interface or mobile app ' ||
   E'to control and monitor your smart home devices from anywhere in the world. ' ||
   E'As you progress, you''ll delve into home automation principles ' ||
   E'and discover the limitless possibilities of the Internet of Things (IoT). ' ||
   E'By the end of this project, you''ll have a fully functional smart home ' ||
   E'that showcases your technical prowess and makes daily life more convenient and efficient.\n\n' ||
   E'### Project Features\n' ||
   E'Here are some of the exciting features you can implement in your smart home automation system:\n\n' ||
   E'| Feature                 | Description                                      |\n' ||
   E'|-------------------------|--------------------------------------------------|\n' ||
   E'| Lighting Control        | Automate lighting based on time or occupancy.    |\n' ||
   E'| Climate Control         | Adjust temperature and humidity remotely.        |\n' ||
   E'| Security Surveillance   | Monitor your home with security cameras.         |\n' ||
   E'| Voice Commands          | Control devices using voice commands.            |\n' ||
   E'| Energy Efficiency       | Optimize energy usage for cost savings.          |\n' ||
   E'| Mobile App Interface    | Build a mobile app for convenient control.       |\n\n' ||
   E'### Implementation Steps\n' ||
   E'Follow these high-level steps to implement your smart home automation system:\n\n' ||
   E'1. **Planning:** Define your requirements and create a system architecture.\n' ||
   E'2. **Hardware Selection:** Choose the right sensors, actuators, and devices.\n' ||
   E'3. **Arduino Programming:** Write code to control devices using Arduino.\n' ||
   E'4. **Raspberry Pi Integration:** Interface with Raspberry Pi for centralized control.\n' ||
   E'5. **Sensor Integration:** Connect various sensors for data collection.\n' ||
   E'6. **Web/Mobile App Development:** Create a user-friendly interface for remote control.\n' ||
   E'7. **Voice Control:** Implement voice recognition and commands.\n' ||
   E'8. **Security and Privacy:** Ensure data security and user privacy.\n' ||
   E'9. **Testing and Debugging:** Test your system thoroughly and address any issues.\n' ||
   E'10. **Deployment:** Install your smart home automation system in your home.\n\n' ||
   E'### Example Arduino Code\n' ||
   E'Here is a simple example of Arduino code to control a smart light bulb:\n\n' ||
   E'```cpp\n' ||
   E'// Smart Light Control\n' ||
   E'void setup() {\n' ||
   E'  pinMode(LED_PIN, OUTPUT); // LED_PIN is the pin connected to the light bulb\n' ||
   E'}\n\n' ||
   E'void loop() {\n' ||
   E'  if (isLightOn) {\n' ||
   E'    digitalWrite(LED_PIN, HIGH); // Turn on the light\n' ||
   E'  } else {\n' ||
   E'    digitalWrite(LED_PIN, LOW); // Turn off the light\n' ||
   E'  }\n' ||
   E'}\n' ||
   E'```\n\n' ||
   E'Feel free to expand on this code and integrate it into your automation system.\n'
  );


-- project DIY Weather Monitoring Station with Raspberry Pi
-- Insert an instance into the workflow_instances table
INSERT INTO workflow_instances (id, current_node_id, active, message, display_type)
VALUES
  (nextval('instance_id_seq'),
   (SELECT id FROM workflow_nodes WHERE workflow_id = (SELECT id FROM workflows WHERE name = 'project_workflow' AND version = 0) AND name = 'planning'),
   true,
   NULL,
   'DEFAULT'
  );

-- Insert a sample project into the projects table with a detailed description
INSERT INTO projects (id, name, short_description, group_id, instance_id, description)
VALUES
  (nextval('project_seq'), 'DIY Weather Monitoring Station with Raspberry Pi', 'Build your own weather monitoring station with Raspberry Pi.', NULL, currval('instance_id_seq'), 
   E'## DIY Weather Monitoring Station with Raspberry Pi\n\n' ||
   E'Welcome to the world of meteorology! In this exciting project, you will embark on a journey to design, build, and deploy ' ||
   E'your very own weather monitoring station using a Raspberry Pi single-board computer. ' ||
   E'Imagine having access to real-time weather data right from your backyard or local area. ' ||
   E'With this project, you''ll gain valuable insights into weather patterns, ' ||
   E'learn about climate monitoring, and contribute to the field of citizen science. ' ||
   E'Your weather station will measure temperature, humidity, atmospheric pressure, wind speed, and more. ' ||
   E'You can access the data remotely via a web interface and even contribute to weather data sharing networks.\n\n' ||
   E'### Project Highlights\n' ||
   E'Here are some of the key features and highlights of your DIY weather monitoring station:\n\n' ||
   E'| Feature                 | Description                                      |\n' ||
   E'|-------------------------|--------------------------------------------------|\n' ||
   E'| Multi-Sensor Capability | Measure temperature, humidity, and more.         |\n' ||
   E'| Real-Time Data Access   | Access weather data from anywhere via the web.    |\n' ||
   E'| Data Visualization      | Create charts and graphs for data visualization.  |\n' ||
   E'| Weather Alerts          | Set up custom weather alerts and notifications.   |\n' ||
   E'| Community Contribution  | Contribute your data to weather networks.         |\n\n' ||
   E'### Implementation Steps\n' ||
   E'Follow these detailed steps to implement your DIY weather monitoring station:\n\n' ||
   E'1. **Planning:** Define the sensors and components you''ll need.\n' ||
   E'2. **Hardware Assembly:** Assemble the weather station hardware.\n' ||
   E'3. **Raspberry Pi Setup:** Set up your Raspberry Pi with the required software.\n' ||
   E'4. **Sensor Integration:** Connect and calibrate the sensors.\n' ||
   E'5. **Data Collection:** Collect weather data from the sensors.\n' ||
   E'6. **Data Storage:** Store the data in a database for analysis.\n' ||
   E'7. **Web Interface:** Create a web interface for data visualization.\n' ||
   E'8. **Alert System:** Implement a weather alert system.\n' ||
   E'9. **Remote Access:** Set up remote access for data viewing.\n' ||
   E'10. **Data Sharing:** Contribute your data to weather networks.\n\n' ||
   E'### Example Python Code (Data Collection)\n' ||
   E'Here is an example Python code snippet to collect temperature and humidity data from a DHT22 sensor:\n\n' ||
   E'```python\n' ||
   E'# Import required libraries\n' ||
   E'import Adafruit_DHT\n' ||
   E'\n' ||
   E'# Set the GPIO pin for the DHT22 sensor\n' ||
   E'sensor_pin = 4\n' ||
   E'\n' ||
   E'# Read data from the sensor\n' ||
   E'humidity, temperature = Adafruit_DHT.read_retry(Adafruit_DHT.DHT22, sensor_pin)\n' ||
   E'\n' ||
   E'if humidity is not None and temperature is not None:\n' ||
   E'    print("Temperature: {:.2f}°C".format(temperature))\n' ||
   E'    print("Humidity: {:.2f}%".format(humidity))\n' ||
   E'else:\n' ||
   E'    print("Failed to retrieve data from the sensor")\n' ||
   E'```\n' ||
   E'Feel free to expand on this code and customize it for your specific sensor setup and data collection needs.\n'
  );


-- project DIY Solar-Powered Water Pump System
-- Insert an instance into the workflow_instances table
INSERT INTO workflow_instances (id, current_node_id, active, message, display_type)
VALUES
  (nextval('instance_id_seq'),
   (SELECT id FROM workflow_nodes WHERE workflow_id = (SELECT id FROM workflows WHERE name = 'project_workflow' AND version = 0) AND name = 'planning'),
   true,
   NULL,
   'DEFAULT'
  );

-- Insert a sample project into the public.projects table with a filled-out implementation report
INSERT INTO public.projects (id, name, short_description, group_id, instance_id, description)
VALUES
  (nextval('project_seq'), 'DIY Solar-Powered Water Pump System', 'Build an eco-friendly water pump system powered by solar energy.', NULL, currval('instance_id_seq'),
   E'## DIY Solar-Powered Water Pump System\n\n' ||
   E'Welcome to the world of sustainable energy! In this ambitious project, we designed and constructed ' ||
   E'a solar-powered water pump system that harnesses the power of the sun to provide a reliable source of water ' ||
   E'for irrigation. This eco-friendly solution not only saves electricity but also reduces ' ||
   E'carbon emissions. Our self-sustaining water supply solution is now operational and serves our garden efficiently.\n\n' ||
   E'### Project Features\n' ||
   E'Here are some of the key features and highlights of our DIY solar-powered water pump system:\n\n' ||
   E'| Feature                 | Description                                      |\n' ||
   E'|-------------------------|--------------------------------------------------|\n' ||
   E'| Solar Panel Array       | Converts sunlight into electricity to power the pump.|\n' ||
   E'| Water Pump              | Pumps water from a source to a destination.       |\n' ||
   E'| Battery Storage         | Stores excess energy for use during cloudy days.   |\n' ||
   E'| Remote Monitoring       | Monitors system performance and water levels remotely.|\n' ||
   E'| Sustainability           | Reduces electricity costs and carbon footprint.     |\n\n' ||
   E'### Implementation Steps\n' ||
   E'Here''s a detailed account of how we implemented our DIY solar-powered water pump system:\n\n' ||
   E'1. **Planning and Design (Date: 2023-05-10)**\n' ||
   E'   - Set project goals and objectives\n' ||
   E'   - Designed the system layout\n' ||
   E'   - Identified components and created a budget\n' ||
   E'   - Considered environmental impact\n\n' ||
   E'2. **Component Selection and Procurement (Date: 2023-05-15)**\n' ||
   E'   - Selected solar panels, pump, battery, and controller\n' ||
   E'   - Ordered and received components\n' ||
   E'   - Documented specifications and user manuals\n' ||
   E'   - Updated the budget with actual costs\n\n' ||
   E'3. **Solar Panel Installation (Date: 2023-05-20)**\n' ||
   E'   - Installed solar panels with the optimal angle and alignment\n' ||
   E'   - Secured panels to a sturdy frame\n' ||
   E'   - Tested the solar panel array\n' ||
   E'   - Celebrated a successful installation\n\n' ||
   E'4. **Pump Installation (Date: 2023-05-25)**\n' ||
   E'   - Set up the water pump near our water source\n' ||
   E'   - Installed the necessary piping and connections\n' ||
   E'   - Checked for leaks and proper water flow\n' ||
   E'   - Pumped the first batch of water with excitement\n\n' ||
   E'5. **Battery and Controller Setup (Date: 2023-05-30)**\n' ||
   E'   - Installed the battery and charge controller\n' ||
   E'   - Connected all electrical components\n' ||
   E'   - Ensured proper safety measures\n' ||
   E'   - System tested with battery power\n\n' ||
   E'6. **Wiring and Connections (Date: 2023-06-05)**\n' ||
   E'   - Wired the solar panels, battery, pump, and controller\n' ||
   E'   - Checked for correct voltage and current\n' ||
   E'   - Ensured electrical safety\n' ||
   E'   - System turned on successfully\n\n' ||
   E'7. **Testing and Calibration (Date: 2023-06-10)**\n' ||
   E'   - Conducted initial system tests\n' ||
   E'   - Calibrated the controller for optimal performance\n' ||
   E'   - Adjusted settings for different weather conditions\n' ||
   E'   - System performing as expected\n\n' ||
   E'8. **Remote Monitoring (Date: 2023-06-15)**\n' ||
   E'   - Set up remote monitoring with data logging\n' ||
   E'   - Developed data reporting and visualization tools\n' ||
   E'   - Monitoring system performance from anywhere\n' ||
   E'   - Excited about the remote access feature\n\n' ||   E'   - Excited about the remote access feature\n\n' ||
   E'9. **Maintenance and Future Plans (Date: 2023-06-20)**\n' ||
   E'   - Created a maintenance plan and schedule\n' ||
   E'   - Troubleshooting and resolving issues\n' ||
   E'   - Future improvements and expansions\n' ||
   E'   - Final thoughts and project conclusion\n\n' ||
   E'This implementation report will serve as a valuable resource for future reference and sharing your ' ||
   E'experience with others interested in similar projects. Best of luck with your DIY solar-powered water pump system!\n'
  );


-- project DIY LED Flashlight
-- Insert an instance into the workflow_instances table
INSERT INTO workflow_instances (id, current_node_id, active, message, display_type)
VALUES
  (nextval('instance_id_seq'),
   (SELECT id FROM workflow_nodes WHERE workflow_id = (SELECT id FROM workflows WHERE name = 'project_workflow' AND version = 0) AND name = 'planning'),
   true,
   NULL,
   'DEFAULT'
  );

-- Insert a sample project into the public.projects table for a beginner-level project
INSERT INTO public.projects (id, name, short_description, group_id, instance_id, description)
VALUES
  (nextval('project_seq'), 'DIY LED Flashlight', 'Build your own LED flashlight from scratch.', NULL, currval('instance_id_seq'),
   E'## DIY LED Flashlight\n\n' ||
   E'Welcome to the world of electronics! In this beginner-friendly project, you will learn how to create ' ||
   E'your very own LED flashlight from scratch. This hands-on project is perfect for those new to electronics ' ||
   E'and soldering. Not only will you have a functional flashlight by the end, but you''ll also gain a solid ' ||
   E'understanding of how basic electronic components work together to create light.\n\n' ||
   E'### Project Features\n' ||
   E'Here are some key features and highlights of your DIY LED flashlight:\n\n' ||
   E'| Feature                 | Description                                      |\n' ||
   E'|-------------------------|--------------------------------------------------|\n' ||
   E'| LED Light               | Produces bright white light for illumination.     |\n' ||
   E'| Battery Power           | Powered by standard AA batteries.               |\n' ||
   E'| Simple Circuitry        | Learn about basic electronic circuits.           |\n' ||
   E'| Soldering Experience    | Gain hands-on soldering skills.                 |\n' ||
   E'| Compact and Portable    | Fits in your pocket for on-the-go lighting.     |\n\n' ||
   E'### Technical Overview\n' ||
   E'#### How it Works\n' ||
   E'The DIY LED flashlight operates on a simple electrical circuit. Here''s a high-level overview of how it works:\n' ||
   E'1. **Power Source:** The flashlight is powered by standard AA batteries, typically connected in series to provide ' ||
   E'sufficient voltage to drive the LED.\n' ||
   E'2. **LED (Light Emitting Diode):** The heart of the flashlight is an LED. When current flows through it, ' ||
   E'it emits bright white light. LEDs are energy-efficient and have a long lifespan.\n' ||
   E'3. **Switch:** A simple on/off switch controls the flow of electricity. When the switch is closed, ' ||
   E'current flows from the batteries to the LED, illuminating it.\n' ||
   E'4. **Circuit Board:** The components are typically mounted on a small circuit board. This board provides ' ||
   E'connectivity between the battery, LED, and switch.\n' ||
   E'5. **Reflector:** Some flashlights include a reflector to focus and direct the light beam, ' ||
   E'enhancing the flashlight''s effectiveness.\n\n' ||
   E'#### Soldering\n' ||
   E'Soldering is the process of joining electrical components together using solder, a low-melting-point metal alloy. ' ||
   E'In this project, you will solder the connections between the battery, LED, and switch to create a functional circuit.\n\n' ||
   E'### Learnings and Takeaways\n' ||
   E'As a beginner, you will gain valuable knowledge and skills through this project:\n\n' ||
   E'1. **Basic Electronics:** Understand how simple electronic circuits function.\n' ||
   E'2. **Soldering Skills:** Learn the basics of soldering, a fundamental skill in electronics.\n' ||
   E'3. **Component Identification:** Identify and use basic electronic components like resistors, LEDs, and switches.\n' ||
   E'4. **Safety:** Learn safety precautions when working with batteries and soldering equipment.\n' ||
   E'5. **Problem Solving:** Troubleshoot and fix issues that may arise during assembly.\n\n' ||
   E'### Project Materials\n' ||
   E'Here are the materials you''ll need to complete your DIY LED flashlight:\n\n' ||
   E'- 1 LED (Light Emitting Diode)\n' ||
   E'- 1 AA Battery Holder\n' ||
   E'- 1 AA Battery\n' ||
   E'- 1 On/Off Switch\n' ||
   E'- Soldering Iron and Solder\n' ||
   E'- Wire for Connections\n' ||
   E'- Small Circuit Board (optional)\n\n' ||
   E'Enjoy building your DIY LED flashlight, and don''t forget to share your success with fellow makers!\n'
  );


-- project DIY Raspberry Pi Smart Mirror
-- Insert an instance into the workflow_instances table
INSERT INTO workflow_instances (id, current_node_id, active, message, display_type)
VALUES
  (nextval('instance_id_seq'),
   (SELECT id FROM workflow_nodes WHERE workflow_id = (SELECT id FROM workflows WHERE name = 'project_workflow' AND version = 0) AND name = 'planning'),
   true,
   NULL,
   'DEFAULT'
  );

-- Insert a sample project into the public.projects table with an extensive description
INSERT INTO public.projects (id, name, short_description, group_id, instance_id, description)
VALUES
  (nextval('project_seq'), 'DIY Raspberry Pi Smart Mirror', 'Create a futuristic smart mirror using a Raspberry Pi.', NULL, currval('instance_id_seq'),
   E'## DIY Raspberry Pi Smart Mirror\n\n' ||
   E'Welcome to the world of innovation and technology! In this captivating project, you will embark on a journey ' ||
   E'to design and build your very own Raspberry Pi-powered smart mirror. This project combines the power of ' ||
   E'open-source software, custom hardware, and creative design to transform an ordinary mirror into ' ||
   E'a futuristic information hub. Imagine waking up and having your mirror display the weather forecast, ' ||
   E'news headlines, your daily schedule, and more, all while you prepare for the day. This project ' ||
   E'promises not only a stunning addition to your home but also an opportunity to dive deep into ' ||
   E'programming, electronics, and design.\n\n' ||
   E'### Project Features\n' ||
   E'Here are some of the extraordinary features and highlights of your DIY Raspberry Pi smart mirror:\n\n' ||
   E'| Feature                 | Description                                      |\n' ||
   E'|-------------------------|--------------------------------------------------|\n' ||
   E'| Magic Mirror Software   | Utilize the open-source Magic Mirror software to display customizable widgets. |\n' ||
   E'| Raspberry Pi Brain      | Use a Raspberry Pi as the brains of your smart mirror, running custom scripts. |\n' ||
   E'| Two-Way Mirror Glass    | Achieve the reflective mirror effect with special two-way mirror glass. |\n' ||
   E'| Voice Control           | Add voice recognition to control your mirror hands-free. |\n' ||
   E'| Customizable Widgets    | Display widgets for time, date, weather, news, calendar, and more. |\n\n' ||
   E'### Technical Overview\n' ||
   E'#### Hardware\n' ||
   E'Your Raspberry Pi smart mirror combines several hardware components:\n' ||
   E'1. **Two-Way Mirror Glass:** This special glass allows one-way visibility, creating the mirror effect.\n' ||
   E'2. **Raspberry Pi:** The Raspberry Pi serves as the central computer that runs the Magic Mirror software.\n' ||
   E'3. **LCD Display:** The LCD screen is positioned behind the mirror glass to display information.\n' ||
   E'4. **Microphone and Speaker:** Add a microphone and speaker for voice control and feedback.\n' ||
   E'5. **Frame and Housing:** Create a custom frame and housing for your smart mirror.\n\n' ||
   E'#### Software\n' ||
   E'Your smart mirror is powered by software:\n' ||
   E'1. **Magic Mirror Software:** Magic Mirror is an open-source platform that lets you create customizable ' ||
   E'widgets to display information such as time, date, weather, news, and more.\n' ||
   E'2. **Voice Assistant:** Integrate voice recognition software to control your mirror with voice commands.\n' ||
   E'3. **Custom Scripts:** Write custom scripts to add unique features and functionality to your mirror.\n' ||
   E'4. **Modules and Plugins:** Enhance your mirror with modules and plugins created by the Magic Mirror community.\n\n' ||
   E'### Implementation Steps\n' ||
   E'Follow these steps to create your DIY Raspberry Pi smart mirror:\n\n' ||
   E'1. **Gather Materials:** Acquire the necessary materials, including the Raspberry Pi, LCD display, mirror glass, ' ||
   E'   microphone, speaker, and frame.\n' ||
   E'2. **Install Magic Mirror:** Set up the Magic Mirror software on your Raspberry Pi following the installation ' ||
   E'   instructions provided on the Magic Mirror website.\n' ||
   E'3. **Assemble Hardware:** Build the smart mirror frame, position the LCD display, and mount the mirror glass.\n' ||
   E'4. **Write Custom Scripts:** Create custom scripts to add unique functionality such as voice control and ' ||
   E'   personalized widgets.\n' ||
   E'5. **Voice Recognition:** Integrate voice recognition software like Snowboy or Jasper to enable voice control.\n' ||
   E'6. **Install Modules:** Enhance your mirror with modules and plugins available in the Magic Mirror community.\n' ||
   E'7. **Calibrate and Test:** Calibrate the mirror''s appearance, test voice control, and fine-tune the widgets.\n' ||
   E'8. **Mount and Display:** Mount your smart mirror in a suitable location and enjoy the futuristic display.\n\n' ||
   E'### Learnings and Takeaways\n' ||
   E'As you embark on this exciting project, you will acquire a wealth of knowledge and skills:\n\n' ||
   E'1. **Raspberry Pi Proficiency:** Gain experience in setting up and using Raspberry Pi as a versatile ' ||
   E'   computing platform.\n' ||
   E'2. **Software Development:** Write custom scripts and explore open-source software development.\n' ||
   E'3. **Hardware Assembly:** Learn to assemble and integrate various hardware components.\n' ||
   E'4. **Voice Recognition:** Understand voice recognition technology and its applications.\n' ||
   E'5. **Design and Aesthetics:** Explore the creative aspects of designing a futuristic smart mirror.\n' ||
   E'6. **Community Engagement:** Connect with the Magic Mirror community for ideas and support.\n\n' ||
   E'### Project Materials\n' ||
   E'Here are the materials you''ll need to create your DIY Raspberry Pi smart mirror:\n\n' ||
   E'- Raspberry Pi (Model 3 or higher)\n' ||
   E'- Two-Way Mirror Glass\n' ||
   E'- LCD Display (compatible with Raspberry Pi)\n' ||
   E'- Microphone and Speaker (for voice control)\n' ||
   E'- Frame and Housing Materials\n' ||
   E'- Magic Mirror Software (open-source)\n' ||
   E'- Custom Scripts and Modules\n\n' ||
   E'Embark on this technological adventure and build your very own DIY Raspberry Pi smart mirror. ' ||
   E'Not only will you have a futuristic home decor piece, but you''ll also gain invaluable skills ' ||
   E'in electronics, software development, and design!\n'
  );
