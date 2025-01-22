from flask import Flask, request, jsonify
from flask_cors import CORS

import threading

app = Flask(__name__)
CORS(app)

@app.route('/api/text/process', methods=['POST'])
def process_data():
    data = request.get_json()
    print(f"Received data: {data}")
    return jsonify({"result": "success"})

if __name__ == '__main__':
    threading.Thread(target=lambda: app.run(host='0.0.0.0', port=5000, debug=False)).start()