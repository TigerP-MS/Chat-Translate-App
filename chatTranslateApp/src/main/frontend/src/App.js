import ScreenCapture from './ScreenCapture';
import React from "react";
import './ScreenCapture.css';

function App() {

    return (
        <div>
            <div className="nav">
                <a className="logo" href="/">ChatTranslate</a>
                <div className="nav-menu">
                    <a href="/api/data">Data</a>
                    <a href="/api/data/write">Write</a>
                </div>
            </div>
            <ScreenCapture/>
        </div>
    );
}

export default App;