# app.py
from flask import Flask, request, jsonify


app = Flask(__name__)
if __name__ == "__main__":
    app.run(debug=True)

led = [
    {"id": 1, "status": False},
    {"id": 2, "status": False},
    {"id": 3, "status": 0},
    {"id": 4, "status": 0},
]

irrigation = {"status": 0}
temp = {"status": 0}
light = {"status": 0}
alarm = {"status": False}
bth = {"status": False}
led_sensor = {"status": 0}


def isPresent(id, valuelist, field):
    for element in valuelist:
        if (element[field] == id):
            return True
    return False


@app.get("/led")
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
