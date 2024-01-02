import './App.css';
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import {RecoilRoot} from "recoil";
import Login from "./views/Login";
import Navbar from "./components/Navbar";
import styled from "styled-components";
import React from 'react';
import Register from "./views/Register";

const Content = styled.div`
    width: 90%;
    margin: auto;
`;

function App() {
    return (
        <RecoilRoot>
            <Router>
                <div className="App">
                    <Navbar />
                    <Content>
                        <Routes>
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                        </Routes>
                    </Content>
                </div>
            </Router>
        </RecoilRoot>
    );
}

export default App;
