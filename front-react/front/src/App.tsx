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
import Hall from "./components/Hall";
import EditProfile from "./views/EditProfile";
import ChangePassword from "./views/ChangePassword";
import ScheduleTable from "./components/ScheduleTable";
import NotificationTable from "./components/NotificationTable";
import BanTable from "./components/BanTable";
import ScheduleTableManager from "./components/ScheduleTableManager";

const Content = styled.div`
    width: 90%;
    margin: auto;
`;

function ProfileLoader() {
    const [profile, setProfile] = useRecoilState(profileInfo);

    useEffect(() => {
        const token = localStorage.getItem('jwt');
        const username = localStorage.getItem('username');
        if (token && username) {
            setProfile({'loggedIn':'true', 'username': username,  'jwt': token, 'data': null});
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
                        <Route path="/reservations" element={<ScheduleTable/>}/>
                        <Route path="/manager" element={<ScheduleTableManager/>}/>
                        <Route path="/edit-profile" element={<EditProfile/>}/>
                        <Route path="/change-password" element={<ChangePassword/>}/>
                        <Route path="/halls" element={<Hall/>}/>
                        <Route path="/mail-archive" element={<NotificationTable/>}/>
                        <Route path="/ban-unban" element={<BanTable/>}/>
                    </Routes>
                </Content>
                <Footer/>
            </Router>
        </RecoilRoot>
    );
}

export default App;