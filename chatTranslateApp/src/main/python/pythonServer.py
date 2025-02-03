from flask import Flask, request, Response, jsonify
from flask_cors import CORS
import sys
import signal
from deep_translator import GoogleTranslator
import threading
import json
app = Flask(__name__)
CORS(app)
translator = GoogleTranslator(source='en', target='ko')
@app.route('/api/text/process', methods=['POST'])
def process_data():
    try:
        data = request.get_json()
        translated = translate_message(data)
        return Response(
            response=json.dumps(translated, ensure_ascii=False),
            status=200,
            mimetype="application/json"
        )
    except Exception as e:
        return jsonify({"error": "Internal Server Error : Python Server Error(process_data)"}), 500

def translate_message(data):
    messages = []
    untranslated_indices = []

    for i, message in enumerate(data['data']):
        if message.get('isTranslated', 0) == 0:
            messages.append(message['message'])
            untranslated_indices.append(i)

    if messages:
        translated = translator.translate_batch(messages)

        for i, idx in enumerate(untranslated_indices):
            data['data'][idx]['message'] = translated[i]

    return data

# @app.route('/api/text/get_translated', methods=['GET'])
# def send_data():
#     print(session, flush=True)
#     if 'data' in session:
#         return jsonify(session['data'])
#     return jsonify({"error": "No data available."}), 404


def signal_handler(sig, frame):
    print("Shutting down Python server...")
    sys.exit(0)

if __name__ == '__main__':
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)
    threading.Thread(target=lambda: app.run(host='0.0.0.0', port=5000, debug=False)).start()