// src/ScreenCapture.js
import React, { useRef, useState, useEffect } from 'react';
import { createWorker } from 'tesseract.js';
import './ScreenCapture.css';

const ScreenCapture = () => {
    const videoRef = useRef(null);
    const canvasRef = useRef(null);
    const streamRef = useRef(null);
    const workerRef = useRef(null);
    const [isCapturing, setIsCapturing] = useState(false);
    const [extractedText, setExtractedText] = useState('');
    const [translatedText, setTranslatedText] = useState({});

    useEffect(() => {
        const initializeWorker = async () => {
            workerRef.current = await createWorker('eng');
            await workerRef.current.setParameters({
                tessedit_char_whitelist: 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,/;\'"!?()[]:<>-_=+*&%$#@ \n',
                preserve_interword_spaces: '1',
            });
        };
        initializeWorker();

        return () => {
            if (workerRef.current) {
                workerRef.current.terminate();
            }
        };
    }, []);

    const startScreenCapture = async () => {
        try {
            const stream = await navigator.mediaDevices.getDisplayMedia({
                video: true,
                audio: false,
            });
            streamRef.current = stream;
            videoRef.current.srcObject = stream;
            setIsCapturing(true);
        } catch (error) {
            console.error("Error starting screen capture:", error);
        }
    };

    const stopScreenCapture = () => {
        if (streamRef.current) {
            const tracks = streamRef.current.getTracks();
            tracks.forEach(track => track.stop());
            streamRef.current = null;
        }
        if (videoRef.current) {
            videoRef.current.srcObject = null;
        }
        setIsCapturing(false);
    };

    useEffect(() => {
        let ocrInterval;

        if (isCapturing && workerRef.current) {
            ocrInterval = setInterval(async () => {
                if (streamRef.current && canvasRef.current) {
                    const canvas = canvasRef.current;
                    const context = canvas.getContext('2d');

                    canvas.width = videoRef.current.videoWidth;
                    canvas.height = videoRef.current.videoHeight;
                    context.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);

                    const { data: { text } } = await workerRef.current.recognize(canvas);
                    setExtractedText(text);
                }
            }, 5000);
        }

        return () => clearInterval(ocrInterval);
    }, [isCapturing]);

    useEffect(() => {
        if (extractedText)
            sendToServer(extractedText);
    }, [extractedText]);

    const sendToServer = (data) => {
        fetch("http://localhost:8080/api/text/translate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        })
            .then(r => r.text())
            .then(response => {
                setTranslatedText(JSON.parse(response));
            });
    };

    return (
        <div className="screen-capture-container">
            <div className="button-container">
                <button className="capture-button"
                        onClick={isCapturing ? stopScreenCapture : startScreenCapture}>
                    {isCapturing ? "Stop Capture" : "Start Capture"}
                </button>
            </div>

            <video ref={videoRef} autoPlay className="video-element" />

            <canvas ref={canvasRef} style={{ display: 'none' }} />

            <div className="translation-ocr-results">
                <div className="ocr-results">
                    <h2>추출 :</h2>
                    <p style={{ whiteSpace: 'pre-line' }}>{extractedText}</p>
                </div>
                <div className="arrow">➡</div>
                <div className="translation-results">
                    <h2>번역 :</h2>
                    <p>{translatedText?.data?.map((item) => (
                        <span key={item.id}>
                            [{item.time}] {item.username} : {item.message}
                            <br/>
                        </span>
                    ))}</p>
                </div>
            </div>
        </div>
    );
};

export default ScreenCapture;
