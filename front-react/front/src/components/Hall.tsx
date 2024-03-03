import React, { useState, useEffect } from 'react';
import EditHallForm from "../views/EditHall";
import { decodeJwt } from '../util/decoder';
import Cookies from "js-cookie";

interface Hall {
    id: number;
    name: string;
    description: string;
    coaches: number;
    managerId: number;
}

const jwt = Cookies.get('jwt');

const Hall = () => {
    const [halls, setHalls] = useState<Hall[]>([]);
    const [selectedHall, setSelectedHall] = useState<Hall | null>(null);
    const [managerId, setManagerId] = useState<number | null>(null);

    const handleEditClick = (hall: Hall) => {
        setSelectedHall(hall);
    };

    useEffect(() => {
        if (jwt) {
            const decoded = decodeJwt(jwt);
            setManagerId(decoded.id);
        }
    }, [window.location.pathname]);

    useEffect(() => {
        if (managerId !== null) {
            fetch(process.env.REACT_APP_HALL_SERVICE_URL + '', {
                headers: {
                    'Authorization': 'Bearer ' + jwt
                }
            })
                .then(response => response.json())
                .then(data => {
                    const filteredHalls = data.data.content.filter((hall: Hall) => hall.managerId === managerId);
                    setHalls(filteredHalls);
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
    }, [window.location.pathname, managerId]);

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
            {jwt ? (selectedHall && <EditHallForm hall={selectedHall} jwt={jwt} />) : (<div></div>)}
        </div>
    );
}

export default Hall;