import React from "react";
import './ScreenCapture.css';

const Navbar = () => {
    return (
        <div className="nav">
            <a className="logo" href="/">ChatTranslate</a>
            <div className="nav-menu">
                <a href="/api/data">Data</a>
                <a href="/api/data/write">Write</a>
            </div>
        </div>
    )
}

export default Navbar;