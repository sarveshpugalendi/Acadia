# Project Acadia

**Autonomous Air Caliber using IoT and Android**

## Overview

Project Acadia is an innovative solution designed to purify the air using IoT and Android technologies. It employs a combination of hardware sensors and an Android application to monitor and improve air quality autonomously.

![Screenshot 1](/Screenshots/Splashscreen.png)

## Features

- **Air Purification**: The hardware setup includes two fans and a filter to clean the air.
- **Real-time Monitoring**: Equipped with sensors to measure air pollutants, CO2 levels, CO levels, and outside temperature.
- **Data Transmission**: Utilizes an ESP-2331 WiFi module to transfer sensor data to the ThingSpeak cloud.
- **Android Application**: The Acadia app retrieves data from ThingSpeak using Retrofit and displays it using the MPAndroidChart library. It also predicts future values using simple regression and sends notifications when pollutant levels cross predefined thresholds.

![Screenshot 1](/Screenshots/Home.png)

## Hardware Components

- **Fans**: Two fans are positioned at the top and bottom of the device to facilitate air circulation.
- **Filter**: Located between the fans to purify the air as it passes through.
- **Sensors**: Four sensors are used to measure:
  - Air pollutants
  - CO2 levels
  - CO levels
  - Outside temperature
- **ESP-2331 WiFi Module**: Sends sensor data to the ThingSpeak cloud for analysis and monitoring.

## Software Components

- **ThingSpeak Cloud**: Stores and provides access to the sensor data.
- **Retrofit**: Used in the Android app to fetch data from ThingSpeak.
- **MPAndroidChart**: Library for plotting the data retrieved from ThingSpeak.
- **Simple Regression**: Algorithm used to predict future values of air quality metrics.
- **Notifications**: The app sends alerts when pollutant levels exceed set thresholds.

## How It Works

1. **Data Collection**: The hardware sensors continuously monitor air quality parameters.
2. **Data Transmission**: The ESP-2331 WiFi module sends the collected data to the ThingSpeak cloud.
3. **Data Retrieval**: The Acadia Android app uses Retrofit to fetch the data from ThingSpeak.
4. **Data Visualization**: The app uses MPAndroidChart to plot the data for easy visualization.
5. **Prediction and Alerts**: The app employs simple regression to predict future air quality values and sends notifications if pollutant levels cross safe thresholds.

## Getting Started

### Prerequisites

- **Hardware**: Ensure you have the necessary hardware components (fans, filter, sensors, ESP-2331 WiFi module).
- **ThingSpeak Account**: Set up a ThingSpeak account to store and access your sensor data.
- **Android Studio**: Install Android Studio for developing and testing the Android application.

### Installation

1. **Hardware Setup**: Assemble the hardware components as per the design specifications.
2. **ThingSpeak Setup**: Configure your ThingSpeak account and channels to receive data from the ESP-2331 module.
3. **Android Application**:
   - Clone the Acadia app repository.
   - Open the project in Android Studio.
   - Configure Retrofit with your ThingSpeak API keys.
   - Build and run the application on an Android device.

## Usage

- **Monitoring Air Quality**: Use the Acadia app to view real-time air quality metrics.
- **Notifications**: Receive alerts when pollutant levels exceed safe limits.
- **Data Analysis**: Visualize historical data and predict future trends using the app's built-in tools.

## Contributing

Contributions are welcome! Please fork the repository and submit pull requests for any enhancements or bug fixes.


## Acknowledgements

- **Retrofit**: For simplifying HTTP requests.
- **MPAndroidChart**: For providing powerful charting capabilities.
- **ThingSpeak**: For enabling IoT data storage and retrieval.
