import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { profileInfo } from '../atoms/loggedAtom';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import {useRecoilState, useRecoilValue} from "recoil";

function CustomNavbar() {
    const [profile, setProfile] = useRecoilState(profileInfo);

    return (
        <Navbar expand="lg" className="navbar-dark bg-dark">
            <Container>
                <Navbar.Brand as={Link} to="/">Ahiley Gym</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {profile.loggedIn === 'true' ? (
                            <>
                                <Nav.Link as={Link} to="/">All trainings</Nav.Link>
                                <Nav.Link as={Link} to="/">Scheduled trainings</Nav.Link>
                                <NavDropdown title="Profile" id="basic-nav-dropdown">
                                    <NavDropdown.Item as={Link} to="/edit-profile">Edit Profile</NavDropdown.Item>
                                    <NavDropdown.Divider />
                                    <NavDropdown.Item as={Link} to="/" onClick={() => setProfile(prevProfile => ({ ...prevProfile, loggedIn: 'false' }))}>Log Out</NavDropdown.Item>
                                </NavDropdown>
                            </>
                            ) : (
                            <>
                                <Nav.Link as={Link} to="/login">Login</Nav.Link>
                                <Nav.Link as={Link} to="/register">Register</Nav.Link>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default CustomNavbar;