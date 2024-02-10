import React, { useState } from 'react';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';

interface Hall {
    id: number;
    name: string;
    description: string;
    coaches: number;
    managerId: number;
}

interface EditHallFormProps {
    hall: Hall;
    jwt: string;
}

const EditHallForm: React.FC<EditHallFormProps> = ({ hall, jwt }) => {
    const [name, setName] = useState(hall.name);
    const [description, setDescription] = useState(hall.description);
    const [coaches, setCoaches] = useState(hall.coaches);
    const [isFormVisible, setIsFormVisible] = useState(true);

    const onSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        const updatedHall = {
            id: hall.id,
            name,
            description,
            coaches,
            managerId: hall.managerId
        };

        fetch(process.env.REACT_APP_HALL_SERVICE_URL + '', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Authorization: jwt
            },
            body: JSON.stringify(updatedHall)
        })
            .then(response => {
                if (response.status === 200) {
                    alert('Hall successfully updated');
                    setIsFormVisible(false);
                } else {
                    alert('There was an error updating the hall');
                }
                return response.json();
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    };

    return isFormVisible ? (
        <Form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            <Form.Group className="mb-3" controlId="formHorizontalName">
                <Form.Label style={{ color: 'white' }}>Name</Form.Label>
                <Form.Control style={{ width: '300px' }} type="text" value={name} onChange={e => setName(e.target.value)} />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formHorizontalDescription">
                <Form.Label style={{ color: 'white' }}>Description</Form.Label>
                <Form.Control style={{ width: '300px' }} type="text" value={description} onChange={e => setDescription(e.target.value)} />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formHorizontalCoaches">
                <Form.Label style={{ color: 'white' }}>Coaches</Form.Label>
                <Form.Control style={{ width: '300px' }} type="number" value={coaches} onChange={e => setCoaches(parseInt(e.target.value))} />
            </Form.Group>

            <Button type="submit">Submit</Button>
        </Form>
    ) : null;
};

export default EditHallForm;