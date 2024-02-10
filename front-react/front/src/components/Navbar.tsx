import React from 'react';
import { Link } from 'react-router-dom';
import { profileInfo } from '../atoms/loggedAtom';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import { useRecoilState } from "recoil";
import { decodeJwt } from '../util/decoder';

function CustomNavbar() {
    const [profile, setProfile] = useRecoilState(profileInfo);
    let role;

    if (profile.jwt) {
        const decoded = decodeJwt(profile.jwt);
        role = decoded.role;
    }

    const handleLogout = () => {
        setProfile(prevProfile => ({ ...prevProfile, loggedIn: 'false' }));
        localStorage.removeItem('jwt');
        document.cookie = "jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        window.location.href = '/';
    };

    return (
        <Navbar expand="lg" className="navbar-dark bg-dark">
            <Container>
                <Navbar.Brand as={Link} to="/">Ahiley Gym</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {profile.loggedIn === 'true' ? (
                            <>
                                <Nav.Link as={Link} to="/appointments">All trainings</Nav.Link>
                                {role === 'USER' && <Nav.Link as={Link} to="/reservations">Scheduled trainings</Nav.Link>}
                                {role === 'MANAGER' && <Nav.Link as={Link} to="/manager">Scheduled trainings</Nav.Link>}
                                {role === 'MANAGER' && <Nav.Link as={Link} to="/halls">Hall</Nav.Link>}
                                {role === 'ADMIN' && (
                                    <NavDropdown title="Admin Panel" id="admin-nav-dropdown">
                                        <NavDropdown.Item as={Link} to="/mail-archive">Mail Archive</NavDropdown.Item>
                                        <NavDropdown.Item as={Link} to="/ban-unban">Ban/Unban User</NavDropdown.Item>
                                    </NavDropdown>
                                )}
                                <NavDropdown title="Profile" id="basic-nav-dropdown">
                                    <NavDropdown.Item as={Link} to="/edit-profile">Edit Profile</NavDropdown.Item>
                                    <NavDropdown.Item as={Link} to="/change-password">Change Password</NavDropdown.Item>
                                    <NavDropdown.Divider />
                                    <NavDropdown.Item as={Link} to="/" onClick={
                                        () => { handleLogout(); }
                                    }>Log Out</NavDropdown.Item>
                                </NavDropdown>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login">Login</Nav.Link>
                                <Nav.Link as={Link} to="/register">Register</Nav.Link>
                            </>
                        )}
                    </Nav>
                    {profile.loggedIn === 'true' && (
                        <Nav className="ml-auto">
                            <Nav.Item className="navbar-text">
                                Hello, {profile.username}
                            </Nav.Item>
                        </Nav>
                    )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default CustomNavbar;