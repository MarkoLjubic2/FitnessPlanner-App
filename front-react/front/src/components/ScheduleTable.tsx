import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useRecoilValue } from 'recoil';
import { profileInfo } from '../atoms/loggedAtom';

interface Reservation {
    id: number;
    date: string;
    startTime: number;
    endTime: number;
    trainingId: number;
    clientId: number;
    day: string;
    trainingName: string;
}

const ScheduleTable = () => {
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const profile = useRecoilValue(profileInfo);

    const getTrainingName = async (trainingId: number) => {
        const response = await fetch(process.env.REACT_APP_TRAINING_SERVICE_URL + '/' + trainingId);
        const data = await response.json();
        return data.data.name;
    }

    useEffect(() => {
        const jwt = localStorage.getItem('jwt');
        if (jwt) {
            const base64Url = jwt.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));

            const decoded = JSON.parse(jsonPayload);
            const userId = decoded.id;

            fetch(process.env.REACT_APP_RESERVATION_SERVICE_URL + '', {
                headers: {
                    'Authorization': jwt
                }
            })
                .then(response => response.json())
                .then(data => {
                    const userReservations = data.data.content.filter((reservation: Reservation) => reservation.clientId === userId);
                    const userReservationsWithTrainingName = userReservations.map(async (reservation: Reservation) => {
                        const trainingName = await getTrainingName(reservation.trainingId);
                        return { ...reservation, trainingName };
                    });
                    Promise.all(userReservationsWithTrainingName).then(setReservations);
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
    }, []);

    return (
        <div>
            <h1 style={{ color: 'white', textAlign: 'center' }}>My Reservations</h1>
            <table className="table rounded-table">
                <thead>
                <tr>
                    <th>Day</th>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Training</th>
                </tr>
                </thead>
                <tbody>
                {reservations.map((reservation: Reservation) => (
                    <tr key={reservation.id}>
                        <td>{reservation.day}</td>
                        <td>{reservation.date}</td>
                        <td>{reservation.startTime}</td>
                        <td>{reservation.endTime}</td>
                        <td>{reservation.trainingName}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default ScheduleTable;