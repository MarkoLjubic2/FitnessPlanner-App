import React, { useState, useEffect } from 'react';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { profileInfo } from '../atoms/loggedAtom';

interface Notification {
    id: number;
    mail: string;
    typeId: number;
    body: string;
    time: string;
}

const NotificationTable = () => {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const profile = useRecoilValue(profileInfo);
    const setProfile = useSetRecoilState(profileInfo);

    useEffect(() => {
        const storedJwt = localStorage.getItem('jwt');
        if (storedJwt) {
            setProfile(prevProfile => ({ ...prevProfile, jwt: storedJwt }));
        }
        setIsLoading(false);
    }, [setProfile]);

    useEffect(() => {
        if (!isLoading && profile.jwt) {
            localStorage.setItem('jwt', profile.jwt);

            fetch(process.env.REACT_APP_NOTIFICATION_SERVICE_URL + '', {
                headers: {
                    'Authorization': profile.jwt
                }
            })
                .then(response => response.json())
                .then(data => {
                    setNotifications(data.data.content);
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
    }, [profile, isLoading]);

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