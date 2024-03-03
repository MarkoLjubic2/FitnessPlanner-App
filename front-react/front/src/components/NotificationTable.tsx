import React, { useState, useEffect } from 'react';
import Cookies from "js-cookie";

interface Notification {
    id: number;
    mail: string;
    typeId: number;
    body: string;
    time: string;
}

const jwt = Cookies.get('jwt');

const NotificationTable = () => {
    const [notifications, setNotifications] = useState<Notification[]>([]);

    useEffect(() => {
        if (!jwt) return;

        fetch(process.env.REACT_APP_NOTIFICATION_SERVICE_URL + '', {
            headers: {
                'Authorization': 'Bearer ' + jwt
            }
        })
            .then(response => response.json())
            .then(data => {
                setNotifications(data.data.content);
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }, []);

    return (
        <div>
            <h1 style={{ color: 'white', textAlign: 'center' }}>Mail Archive</h1>
            <table className="table rounded-table">
                <thead>
                <tr>
                    <th>Mail</th>
                    <th>Type</th>
                    <th>Body</th>
                    <th>Time</th>
                </tr>
                </thead>
                <tbody>
                {notifications.map((notification: Notification) => (
                    <tr key={notification.id}>
                        <td>{notification.mail}</td>
                        <td>{notification.typeId}</td>
                        <td>{notification.body}</td>
                        <td>{notification.time}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default NotificationTable;