// src/ScreenCapture.js
import React, { useRef, useState, useEffect } from 'react';
import Tesseract from 'tesseract.js';

const ScreenCapture = () => {
    const videoRef = useRef(null);
    const canvasRef = useRef(null); // OCR용 캔버스 참조
    const [isCapturing, setIsCapturing] = useState(false);
    const [extractedText, setExtractedText] = useState(''); // OCR 결과 저장
    const [translatedText, setTranslatedText] = useState({});

    // 화면 캡처 시작
    const startScreenCapture = async () => {
        try {
            const stream = await navigator.mediaDevices.getDisplayMedia({
                video: true,
                audio: false,
            });
            videoRef.current.srcObject = stream;
            setIsCapturing(true);
        } catch (error) {
            console.error("Error starting screen capture:", error);
        }
    };

    // 화면 캡처 중지
    const stopScreenCapture = () => {
        const stream = videoRef.current.srcObject;
        const tracks = stream.getTracks();
        tracks.forEach(track => track.stop());
        videoRef.current.srcObject = null;
        setIsCapturing(false);
    };

    // 일정 주기로 OCR 실행
    useEffect(() => {
        let ocrInterval;

        if (isCapturing) {
            ocrInterval = setInterval(async () => {
                if (videoRef.current && canvasRef.current) {
                    // 비디오 프레임을 캔버스에 그리기
                    const canvas = canvasRef.current;
                    const context = canvas.getContext('2d');
                    canvas.width = videoRef.current.videoWidth;
                    canvas.height = videoRef.current.videoHeight;
                    context.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);

                    // Tesseract를 통해 OCR 수행
                    const { data: { text } } = await Tesseract.recognize(canvas, 'eng');
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
            body: JSON.stringify(data)
        })
        .then(r => r.text())
        .then(response => {
            setTranslatedText(JSON.parse(response));
        });
    };

    const sendExtractedTextToServer = () => {
        if (extractedText.trim() !== '') {
            sendToServer(extractedText);
        } else {
            console.warn("No extracted text to send!");
        }
    };

    return (
        <div>
            <h1>Screen Capture with OCR</h1>
            <button onClick={isCapturing ? stopScreenCapture : startScreenCapture}>
                {isCapturing ? "Stop Capture" : "Start Capture"}
            </button>

            <button
                onClick={sendExtractedTextToServer}
                style={{marginLeft: "20px"}}>
                서버요청
            </button>

            {/* 비디오 요소 */}
            <video ref={videoRef} autoPlay style={{width: '100%', marginTop: '20px'}}/>

            {/* OCR 캔버스 (숨김 처리) */}
            <canvas ref={canvasRef} style={{display: 'none'}}/>

            {/* OCR 결과 표시 */}
            <h2>추출 : </h2>
            <p>{extractedText.split('\n').map((line, index) => (
                <React.Fragment key={index}>
                    {line}
                    <br/>
                </React.Fragment>
            ))}</p>

            <h2>번역 : </h2>
            <p>{translatedText?.data?.map ((item) => (
                <span key={item.id}>
                    [{item.time}] {item.username} : {item.message}
                    <br/>
                </span>
            ))}</p>
        </div>
    );
};

export default ScreenCapture;
