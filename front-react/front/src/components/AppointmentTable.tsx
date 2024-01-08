import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useRecoilValue } from 'recoil';
import { profileInfo } from '../atoms/loggedAtom';

interface Appointment {
    id: number;
    date: string;
    startTime: string;
    endTime: string;
    trainingId: number;
    clientId: number;
    day: string;
    maxClients: number;
    currentClients: number;
    open: boolean;
    trainingName: string;
}

interface Reservation {
    id: number;
    date: string;
    startTime: number;
    endTime: number;
    trainingId: number;
    clientId: number;
    day: string;
    canceled: boolean;
}

const AppointmentTable = () => {
    const [appointments, setAppointments] = useState<Appointment[]>([]);
    const profile = useRecoilValue(profileInfo);
    const [reservations, setReservations] = useState<Reservation[]>([]);

    const getTrainingName = async (trainingId: number) => {
        const response = await fetch(process.env.REACT_APP_TRAINING_SERVICE_URL + '/' + trainingId);
        const data = await response.json();
        return data.data.name;
    }

    const scheduleTraining = async (appointment: Appointment) => {
        const base64Url = profile.jwt.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const decoded = JSON.parse(jsonPayload);
        appointment.clientId = decoded.id;

        const response = await fetch(process.env.REACT_APP_RESERVATION_SERVICE_URL + '/schedule', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + profile.jwt
            },
            body: JSON.stringify(appointment)
        });

    }

    useEffect(() => {
        fetch(process.env.REACT_APP_RESERVATION_SERVICE_URL + '', {
            headers: {
                'Authorization': profile.jwt
            }
        })
            .then(response => response.json())
            .then(data => setReservations(data.data.content))
            .catch(error => console.error('There was an error!', error));
    }, []);

    const isReserved = (appointment: Appointment) => {
        const base64Url = profile.jwt.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const decoded = JSON.parse(jsonPayload);
        return reservations.some(reservation =>
            reservation.startTime === Number(appointment.startTime) &&
            reservation.endTime === Number(appointment.endTime) &&
            reservation.date === appointment.date &&
            reservation.trainingId === appointment.trainingId &&
            reservation.clientId === decoded.id &&
            !reservation.canceled
        );
    };

    useEffect(() => {
        fetch(process.env.REACT_APP_APPOINTMENT_SERVICE_URL + '')
            .then(response => response.json())
            .then(data => {
                const appointmentsWithTrainingName = data.data.content.map(async (appointment: Appointment) => {
                    const trainingName = await getTrainingName(appointment.trainingId);
                    return { ...appointment, trainingName };
                });
                Promise.all(appointmentsWithTrainingName).then(setAppointments);
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }, []);

    return (
        <div>
            <h1 style={{ color: 'white', textAlign: 'center' }}>Appointments</h1>
            <table className="table rounded-table">
                <thead>
                <tr>
                    <th>Day</th>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Training</th>
                    <th>Max Clients</th>
                    <th>Current Clients</th>
                    <th>Is Open</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {appointments.map((appointment: Appointment) => (
                    <tr key={appointment.id}>
                        <td>{appointment.day}</td>
                        <td>{appointment.date}</td>
                        <td>{appointment.startTime}</td>
                        <td>{appointment.endTime}</td>
                        <td>{appointment.trainingName}</td>
                        <td>{appointment.maxClients}</td>
                        <td>{appointment.currentClients}</td>
                        <td>{appointment.open ? 'Yes' : 'No'}</td>
                        <td>
                            <button disabled={isReserved(appointment)}
                                    onClick={() => scheduleTraining(appointment)}>Schedule Training
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default AppointmentTable;