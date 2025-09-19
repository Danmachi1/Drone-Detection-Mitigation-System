# Drone-Detection-Mitigation-System
V1 Command and Control Interface with different behavior models for a drone system, Several features have been simplified or abstracted. Specifically, direct hardware serial readers and high-performance C/C++ libraries for Kalman filtering have been replaced with simplified Java stubs    

This repository contains the complete source code for a modular, multi-sensor system designed for the detection, tracking, and mitigation of unauthorized drones. The system integrates various sensor types, including RF, visual, thermal, and acoustic, to provide a comprehensive, real-time threat assessment. It features a robust data persistence layer, a command-and-control interface, and a rich, interactive UI for operator situational awareness. The architecture is built for extensibility, allowing for easy integration of new hardware and software modules.

Note: Due to the complexity and proprietary nature of some components, several features have been simplified or abstracted. Specifically, direct hardware serial readers and high-performance C/C++ libraries for Kalman filtering have been replaced with simplified Java stubs or simulated approximations. This version is intended for demonstration and educational purposes.

Source Code Breakdown
sensors.plugins Package
This package contains the core sensor integration logic. Each class represents a specific sensor type, adhering to the SensorPlugin interface for a unified data polling mechanism.

RealRFPlugin.java: A placeholder for a real-world RF detection system, intended to detect drone controller and telemetry signals.

RealRfSensorPlugin.java: A more detailed stub for an SDR-based RF front-end. It defaults to a synthetic simulation and includes placeholders for connecting to real hardware.

RealSonarPlugin.java: A stub for an actual sonar module, simulating low-altitude drone detection.

RealThermalPlugin.java: A placeholder for a thermal camera integration, simulating the detection of drone heat signatures.

RealVisualPlugin.java: A stub for a computer vision pipeline, such as one using OpenCV or YOLO. It simulates a visual drone detection.

ReplaySensorPlugin.java: A non-real-time plugin that reads pre-recorded sensor data from a CSV file, enabling deterministic replay of past events for testing.

RFSignalSensorPlugin.java: A simulation plugin that generates a drifting RF signal, mimicking a moving drone and providing a test case for tracking algorithms.

SimulatedAcousticPlugin.java: Simulates a sound-based detection system, generating data from a "microphone array" to test the acoustic data pipeline.

SimulatedRadarPlugin.java: A sophisticated simulator that models a swarm of threats with different behaviors (Scout, Aggressor, Decoy), providing realistic test data for the system's tracking and threat assessment capabilities.

storage Package
This package manages all data persistence for the system, from configuration to sensor logs.

ConfigLoader.java: A utility that loads application settings from a JSON file into the central Config class, handling strings, maps, and zone data.

CsvExporter.java: A simple writer for appending sensor records to a CSV file for lightweight logging and quick analysis.

DataPersistenceEngine.java: The main facade for data storage. It coordinates writing data to both a SQLite database (TrackDatabase) and an optional CSV file (CsvExporter).

JsonStorageManager.java: A general-purpose helper for reading and writing any Java object to a JSON file. Used by other classes for saving complex data structures.

MissionRecorder.java: A utility that uses JsonStorageManager to save mission plans to a timestamped JSON file, allowing for the re-execution of specific mission scenarios.

ProfileSerializer.java: Manages the saving and loading of operator profiles (preferences, settings) in a batch to a JSON file.

ReplaySaver.java: A class that captures and saves an entire simulation run to a CSV file, which can later be used by the ReplaySensorPlugin.

TrackDatabase.java: A JDBC wrapper for an on-disk SQLite database. It provides the primary long-term storage for all SensorDataRecords, enabling historical data analysis.

ui.control Package
CommandRouter.java: This is the application's command hub. It acts as a router, forwarding user commands from the UI to the appropriate backend services and logic, ensuring a clear separation of concerns.

utils Packages
This collection of packages contains various utility classes that provide foundational services used throughout the system.

utils.alerts:

AlertManager.java: Manages the creation and distribution of system alerts and notifications.

utils.geo:

CoordinateConverter.java: Handles transformations between different coordinate systems.

DistanceCalculator.java: A utility for computing distances between geographical points.

ElevationUtils.java: Provides functions for handling altitude and terrain data.

GeoFenceUtils.java: Contains logic for detecting if coordinates fall within defined geographical boundaries.

utils.logging:

LiveLogViewer.java: Manages the real-time display of log entries.

LogEntry.java: Defines the data structure for a single log message.

LogManager.java: The central logging service for the application.

LogPlaybackEngine.java: Enables the replaying of historical log data.

LogStorageManager.java: Handles the persistent storage of log files.

SystemLogger.java: Provides a convenient, static interface for logging.

utils.map:

AreaScanner.java: A helper for map-based area calculations.

ElevationOverlay.java: Manages the visual rendering of elevation data on the map.

MapCacheManager.java: Manages the caching of map tiles for performance.

MapManager.java: The core map component that integrates various map layers.

OfflineTileDownloader.java: A tool for pre-downloading map tiles for offline use.

TileRenderer.java: Renders map data onto the user interface.
