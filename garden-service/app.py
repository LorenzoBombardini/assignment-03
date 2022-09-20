# app.py
from enum import Enum
from pickle import TRUE
import threading
from flask import Flask, request, jsonify
from flask_cors import CORS
import time
import signal
import sys

app = Flask(__name__)
CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'


class RepeatedTimer(object):
    def __init__(self, interval, function, *args, **kwargs):
        self._timer = None
        self.interval = interval
        self.function = function
        self.args = args
        self.kwargs = kwargs
        self.is_running = False
        self.next_call = time.time()
        self.start()

    def _run(self):
        self.is_running = False
        self.start()
        self.function(*self.args, **self.kwargs)

    def start(self):
        if not self.is_running:
            self.next_call += self.interval
            self._timer = threading.Timer(
                self.next_call - time.time(), self._run)
            self._timer.start()
            self.is_running = True

    def stop(self):
        self._timer.cancel()
        self.is_running = False


class SystemStatus (Enum):
    ALARM = 0
    MANUAL = 1
    AUTO = 2


class IrrigationStatus (Enum):
    WORK = 0
    PAUSE = 1
    READY = 2


# environment variables and costants
IRRIGATION_STOP_TIME = 60
IRRIGATION_MAX_EXECUTON_TIME = 30
actualSystemStatus = SystemStatus.AUTO
actualIrrigationStatus = IrrigationStatus.READY
start_irrigation_time = None
start_stop_time = None
mappedTemperature = 0

# api variables
led = [
    {"id": 1, "status": False},
    {"id": 2, "status": False},
    {"id": 3, "status": 0},
    {"id": 4, "status": 0},
]
irrigation = {"status": 0}  # 1-5
temp = {"status": 30}  # celsius
light = {"status": 7}  # 0-7
alarm = {"status": False}
bth = {"status": False}
led_sensor = {"status": 1}  # 0-1
controller_connected = False
mobile_connected = False


def mapTemperature():
    global mappedTemperature
    # map non scalar temperature according to real values temperature
    if temp["status"] > 50:
        mappedTemperature = 5
    elif temp["status"] < 10:
        mappedTemperature = 0
    else:
        mappedTemperature = int(temp["status"] / 10)


def engine():
    global actualSystemStatus
    global actualIrrigationStatus
    global start_stop_time
    global start_irrigation_time
    global controller_connected
    global mobile_connected

    mapTemperature()

    if actualSystemStatus == SystemStatus.AUTO:
        # bth
        if mobile_connected and controller_connected:
            bth["status"] = True
            actualSystemStatus = SystemStatus.MANUAL
        else:
            bth["status"] = False
            actualSystemStatus = SystemStatus.AUTO
            # light
            if light["status"] < 5:
                led[0]["status"] = True
                led[1]["status"] = True
                led[2]["status"] = 4 - light["status"]
                led[3]["status"] = 4 - light["status"]
            else:
                led[0]["status"] = False
                led[1]["status"] = False
                led[2]["status"] = 0
                led[3]["status"] = 0
            if light["status"] < 2 and mappedTemperature > 0 and actualIrrigationStatus == IrrigationStatus.READY:
                # don't put water on the grass with temperature below 10 degrees, the grass will freeze and the pipes will break
                start_irrigation_time = time.time()
                actualIrrigationStatus = IrrigationStatus.WORK
                irrigation["status"] = mappedTemperature

            # alarm
            if mappedTemperature == 5 and actualIrrigationStatus == IrrigationStatus.PAUSE:
                actualSystemStatus = SystemStatus.ALARM
                alarm["status"] = True
                led_sensor["status"] = 0

            if start_irrigation_time != None and (time.time()-start_irrigation_time) > IRRIGATION_MAX_EXECUTON_TIME:
                actualIrrigationStatus = IrrigationStatus.PAUSE
                irrigation["status"] = 0
                start_irrigation_time = None
                start_stop_time = time.time()

            if start_stop_time != None and (time.time()-start_stop_time) > IRRIGATION_STOP_TIME:
                actualIrrigationStatus = IrrigationStatus.READY
                start_stop_time = None

    if alarm["status"] == False and not (mobile_connected and controller_connected):
        actualSystemStatus = SystemStatus.AUTO
        led_sensor["status"] = 1

    # print(threading.get_ident())
    print(controller_connected)
    print(mobile_connected)
    print(actualSystemStatus)
    print(actualIrrigationStatus)
    print("Mapped temperature: " + str(mappedTemperature))


rt = RepeatedTimer(10, engine)


def signal_handler(sig, frame):
    rt.stop()
    print('You pressed Ctrl+C!')
    sys.exit(0)


signal.signal(signal.SIGINT, signal_handler)


def isPresent(id, valuelist, field):
    for element in valuelist:
        if (element[field] == id):
            return True
    return False


@app.get("/led/")
def get_leds():
    return jsonify(led)


@app.get("/light")
def get_light():
    return jsonify(light)


@app.get("/temp")
def get_temp():
    return jsonify(temp)


@app.get("/irrigation")
def get_irrigation():
    return jsonify(irrigation)


@app.get("/alarm")
def get_alarm():
    return jsonify(alarm)


@app.get("/bth")
def get_bth():
    return jsonify(bth)


@app.get("/led/<id>")
def get_led(id):
    id = int(id)
    if (isPresent(id, led, "id")):
        return jsonify(led[int(id)-1])
    return []


@app.post("/alarm")
def set_alarm():
    if request.is_json:
        req = request.get_json()
        status = req["status"]
        status = bool(status)
        alarm["status"] = status
        return alarm, 201
    return {"error": "Request must be JSON"}, 415


@app.post("/controller/bth")
def set_controller_connected():
    global controller_connected
    if request.is_json:
        req = request.get_json()
        status = req["status"]
        status = bool(status)
        controller_connected = status
        return str(controller_connected), 201
    return {"error": "Request must be JSON"}, 415


@app.post("/mobile/bth")
def set_mobile_connected():
    global mobile_connected
    if request.is_json:
        req = request.get_json()
        status = req["status"]
        status = bool(status)
        mobile_connected = status
        return str(mobile_connected), 201
    return {"error": "Request must be JSON"}, 415


@app.post("/sensor")
def set_sensor_values():
    if request.is_json:
        req = request.get_json()
        statustemp = req["temp"]
        statuslight = req["light"]
        statustemp = int(statustemp)
        statuslight = int(statuslight)
        temp["status"] = statustemp
        light["status"] = statuslight
        return str(led_sensor["status"]), 201
    return {"error": "Request must be JSON"}, 415


app.run(debug=False, port=8000, host="0.0.0.0")
