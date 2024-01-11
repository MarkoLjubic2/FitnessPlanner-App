import React, { useEffect, useRef } from 'react';
import '../App.css';

interface MarqueeProps {
    text: string;
}

const Marquee: React.FC<MarqueeProps> = ({ text }) => {
    const marqueeRef = useRef<HTMLParagraphElement>(null);

    useEffect(() => {
        if (marqueeRef.current) {
            const textLength = marqueeRef.current.getClientRects()[0].width;
            const duration = textLength / 200;
            marqueeRef.current.style.animationDuration = `${duration}s`;
        }
    }, [text]);

    return (
        <div className="marquee">
            <p ref={marqueeRef}>{text}</p>
        </div>
    );
}

export default Marquee;