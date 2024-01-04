import './App.css';
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import {RecoilRoot} from "recoil";
import Login from "./views/Login";
import Navbar from "./components/Navbar";
import styled from "styled-components";
import React, {useEffect} from 'react';
import { profileInfo } from './atoms/loggedAtom';
import { useRecoilState } from 'recoil';
import Register from "./views/Register";
import Footer from "./components/Footer";
import AppointmentTable from "./components/AppointmentTable";

const Content = styled.div`
    width: 90%;
    margin: auto;
`;

function ProfileLoader() {
    const [profile, setProfile] = useRecoilState(profileInfo);

    useEffect(() => {
        const token = localStorage.getItem('jwt');
        if (token) {
            setProfile({'loggedIn':'true', 'jwt': token, 'data': null});
        }
    }, []);

    return null;
}

function App() {
    return (
        <RecoilRoot>
            <Router>
                <Navbar/>
                <Content>
                    <ProfileLoader/>
                    <Routes>
                        <Route path="/login" element={<Login/>}/>
                        <Route path="/register" element={<Register/>}/>
                        <Route path="/appointments" element={<AppointmentTable/>}/>
                    </Routes>
                </Content>
                <Footer/>
            </Router>
        </RecoilRoot>
    );
}

export default App;