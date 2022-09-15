# app.py
from flask import Flask, request, jsonify


app = Flask(__name__)
if __name__ == "__main__":
    app.run(debug=True)

countries = [
    {"id": 1, "name": "Thailand", "capital": "Bangkok", "area": 513120},
    {"id": 2, "name": "Australia", "capital": "Canberra", "area": 7617930},
    {"id": 3, "name": "Egypt", "capital": "Cairo", "area": 1010408},
]

led = [
    {"id": 1, "status": False},
    {"id": 2, "status": False},
    {"id": 3, "status": 0},
    {"id": 4, "status": 0},
]

irrigation = {"status": 0}
alarm = {"status": False}
bth = {"status": False}


def isPresent(id,valuelist,field):
    for element in valuelist:
        if (element[field] == id):
            return True
    return False

def _find_next_id():
    return max(country["id"] for country in countries) + 1


@app.get("/led")
def get_leds():
    return jsonify(led)

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
    if (isPresent(id,led,"id")):
        return jsonify(led[int(id)-1])
    return []


@app.post("/alarm")
def add_country():
    if request.is_json:
        req = request.get_json()
        status = req["status"]
        status = bool(status)
        alarm["status"]=status
        return alarm, 201
    return {"error": "Request must be JSON"}, 415
