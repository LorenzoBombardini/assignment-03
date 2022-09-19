# app.py
from enum import Enum
from pickle import TRUE
from time import sleep
from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'


class Status (Enum):
    ALARM = 0
    MANUAL = 1
    AUTO = 2


led = [
    {"id": 1, "status": True},
    {"id": 2, "status": False},
    {"id": 3, "status": 3},
    {"id": 4, "status": 0},
]

STOPS = 5
SLEEP = 30
actualStatus = Status.AUTO

controller_connected = False
mobile_connected = False

irrigation = {"status": 0}  # 1-5
temp = {"status": 0}  # 1-5
light = {"status": 0}  # 0-7
alarm = {"status": False}
bth = {"status": False}
led_sensor = {"status": 1}  # 0-1


@app.before_request
def engine():
    # light
    if light["status"] < 5:
        led[0]["status"] = True
        led[1]["status"] = True
        led[2]["status"] = 4 - light["status"]
        led[3]["status"] = 4 - light["status"]
    if light["status"] < 2:
        irrigation["status"] = 1

    # irrigation
    irrigation["status"] = temp["status"]

    # bth
    if mobile_connected and controller_connected:
        bth["status"] = True
        actualStatus = Status.MANUAL
    else:
        bth["status"] = False
        actualStatus = Status.AUTO

    # alarm
    if temp["status"] == 5:
        actualStatus = Status.ALARM
        alarm["status"] = True
        led_sensor["status"] = 0
    elif alarm["status"] == False and not(mobile_connected and controller_connected):
        actualStatus = Status.AUTO
        led_sensor["status"] = 1

    print(actualStatus)


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
    if request.is_json:
        req = request.get_json()
        status = req["status"]
        status = bool(status)
        controller_connected = status
        return controller_connected, 201
    return {"error": "Request must be JSON"}, 415


@app.post("/mobile/bth")
def set_mobile_connected():
    if request.is_json:
        req = request.get_json()
        status = req["status"]
        status = bool(status)
        mobile_connected = status
        return mobile_connected, 201
    return {"error": "Request must be JSON"}, 415

@app.post("/mobile/alarm")
def set_mobile_connected():
    if request.is_json:
        req = request.get_json()
        status = req["status"]
        status = bool(status)
        alarm["status"] = status
        return alarm, 201
    return {"error": "Request must be JSON"}, 415


@app.post("/sensor")
def set_temp():
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
