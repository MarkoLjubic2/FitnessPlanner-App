import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Cookies from "js-cookie";

interface Reservation {
    id: number;
    date: string;
    startTime: number;
    endTime: number;
    trainingId: number;
    clientId: number;
    day: string;
    trainingName: string;
    canceled: boolean;
}

const jwt = Cookies.get('jwt');

const ScheduleTable = () => {
    const [reservations, setReservations] = useState<Reservation[]>([]);

    const getTrainingName = async (trainingId: number) => {
        const response = await fetch(process.env.REACT_APP_TRAINING_SERVICE_URL + '/' + trainingId, {
            headers: {
                'Authorization': 'Bearer ' + jwt
            }
        });
        const data = await response.json();
        return data.data.name;
    }

    const cancelTraining = async (reservation: Reservation) => {
        const response = await fetch(process.env.REACT_APP_RESERVATION_SERVICE_URL + '/cancel', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + jwt
            },
            body: JSON.stringify(reservation)
        });

        if (response.ok) {
            setReservations(reservations.filter(r => r.id !== reservation.id));
        }
    }

    useEffect(() => {
        if (!jwt) return;

        fetch(process.env.REACT_APP_RESERVATION_SERVICE_URL + '/manager', {
            headers: {
                'Authorization': 'Bearer ' + jwt
            }
        })
            .then(response => response.json())
            .then(async data => {
                if (data && data.data && Array.isArray(data.data.content)) {
                    const reservationsWithTrainingName = await Promise.all(data.data.content.map(async (reservation: Reservation) => {
                        reservation.trainingName = await getTrainingName(reservation.trainingId);
                        return reservation;
                    }));
                    setReservations(reservationsWithTrainingName.filter((reservation: Reservation) => !reservation.canceled));
                } else {
                    console.error('Data is not structured as expected!', data);
                }
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
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
                    <th>Action</th>
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
                        <td>
                            <button onClick={() => cancelTraining(reservation)}>Cancel Training</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default ScheduleTable;