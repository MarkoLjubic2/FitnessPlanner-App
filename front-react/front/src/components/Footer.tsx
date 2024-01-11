import React from 'react';
import '../App.css';
import Marquee from './Marquee';

const Footer = () => {
    const messages = [
        "Every 10th training is free",
        " ---------------------- 20% discount for police and military",
        " ---------------------- Open 24/7",
    ];

    return (
        <footer className="App-footer">
            <Marquee text={messages.join(' ')} />
            Vasilije Jukic & Marko Ljubic © 2024
        </footer>
    );
}

export default Footer;