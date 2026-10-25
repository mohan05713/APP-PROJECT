"""
SRM Green Residency - Water & Resource Anomaly Detection Engine
Python OOP Implementation for Presentation Slide / Viva
Demonstrates: Encapsulation, Inheritance, Polymorphism, and Alert Dispatching
"""

from abc import ABC, abstractmethod
import datetime


# 1. Interface (Alertable) using Python's Abstract Base Class
class Alertable(ABC):
    @abstractmethod
    def trigger_alert(self, message: str) -> None:
        """Dispatches emergency warning notification."""
        pass


# 2. Base Class (ResourceSensor)
class ResourceSensor:
    def __init__(self, sensor_id: str, zone_id: str):
        self._sensor_id = sensor_id        # Protected attribute (Encapsulation)
        self._zone_id = zone_id
        self._is_active = True

    @property
    def zone_id(self) -> str:
        return self._zone_id

    def is_scheduled_usage(self) -> bool:
        """Checks if current time falls under planned resident consumption."""
        # E.g., scheduled morning tank filling / irrigation
        return False

    @abstractmethod
    def detect_anomaly(self) -> bool:
        """Polymorphic method overridden by specific sensor classes."""
        pass


# 3. Derived Subclass (WaterMeter) implementing Encapsulation, Inheritance & Polymorphism
class WaterMeter(ResourceSensor, Alertable):
    def __init__(self, sensor_id: str, zone_id: str, current_flow_rate: float = 0.0, leak_threshold: float = 45.0):
        super().__init__(sensor_id, zone_id)
        self.__current_flow_rate = current_flow_rate   # Private encapsulated field (Liters / min)
        self.__leak_threshold = leak_threshold         # Private threshold

    # Encapsulation via Python @property and setter
    @property
    def current_flow_rate(self) -> float:
        return self.__current_flow_rate

    @current_flow_rate.setter
    def current_flow_rate(self, value: float):
        if value >= 0:
            self.__current_flow_rate = value
        else:
            raise ValueError("Flow rate cannot be negative.")

    # Implementation of Alertable Interface
    def trigger_alert(self, message: str) -> None:
        timestamp = datetime.datetime.now().strftime("%H:%M:%S")
        print(f"[🚨 CRITICAL ALERT | {timestamp}] {message}")

    # Polymorphic Method Override
    def detect_anomaly(self) -> bool:
        """
        Anomaly Detection Rule:
        Triggers alert if flow rate exceeds threshold outside scheduled consumption windows.
        """
        if self.__current_flow_rate > self.__leak_threshold and not self.is_scheduled_usage():
            self.trigger_alert(f"Potential water leakage in zone: {self.zone_id} (Flow: {self.__current_flow_rate} L/min > Limit: {self.__leak_threshold} L/min)")
            return True
        return False


# =====================================================================
# DEMO EXECUTION (For PPT / Viva Output Verification)
# =====================================================================
if __name__ == "__main__":
    print("--- SRM Green Residency: IoT Anomaly Detection Test ---")
    
    # Normal Reading
    meter = WaterMeter(sensor_id="WM-101", zone_id="Block-A Kitchen Main", current_flow_rate=12.5)
    print(f"Zone: {meter.zone_id} | Flow: {meter.current_flow_rate} L/min | Anomaly: {meter.detect_anomaly()}")

    # Leakage Spike Anomaly Reading
    meter.current_flow_rate = 58.2  # Exceeds 45.0 L/min threshold
    print(f"Zone: {meter.zone_id} | Flow: {meter.current_flow_rate} L/min | Anomaly: {meter.detect_anomaly()}")
