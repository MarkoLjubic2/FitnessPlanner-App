import React, { useState, useEffect } from 'react';
import {useRecoilValue} from 'recoil';
import { profileInfo } from '../atoms/loggedAtom';
import EditHallForm from "../views/EditHall";

interface Hall {
    id: number;
    name: string;
    description: string;
    coaches: number;
    managerId: number;
}

const Hall = () => {
    const [halls, setHalls] = useState<Hall[]>([]);
    const [selectedHall, setSelectedHall] = useState<Hall | null>(null);
    const [mid, setMid] = useState<number | null>(null);
    const profile = useRecoilValue(profileInfo);

    const handleEditClick = (hall: Hall) => {
        setSelectedHall(hall);
    };

    useEffect(() => {
        if (profile.jwt) {
            const base64Url = profile.jwt.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));

            const decoded = JSON.parse(jsonPayload);
            setMid(decoded.id);
        }
    }, [window.location.pathname, profile.jwt]);

    useEffect(() => {
        if (mid !== null) {
            fetch(process.env.REACT_APP_HALL_SERVICE_URL + '')
                .then(response => response.json())
                .then(data => {
                    const filteredHalls = data.data.content.filter((hall: Hall) => hall.managerId === mid);
                    console.log(filteredHalls);
                    setHalls(filteredHalls);
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
    }, [window.location.pathname, mid]);

    return (
        <div>
            <h1 style={{ color: 'white', textAlign: 'center' }}>Hall</h1>
            <table className="table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Coaches</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {halls.map((hall: Hall) => (
                    <tr key={hall.id}>
                        <td>{hall.name}</td>
                        <td>{hall.description}</td>
                        <td>{hall.coaches}</td>
                        <td>
                            <button onClick={() => handleEditClick(hall)}>Edit</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {selectedHall && <EditHallForm hall={selectedHall} jwt={profile.jwt} />}
        </div>
    );
}

export default Hall;