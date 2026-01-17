import React, { createContext, useContext, useState } from 'react';
import axios from 'axios';

export const AuthContext = createContext({});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const username = localStorage.getItem('username');
    const id = localStorage.getItem('id');
    
    return token ? { token, role, username, id } : null;
  });

  // Modificăm funcția login să accepte tot obiectul userData
  const login = (token, userData) => {
    localStorage.setItem('token', token);
    localStorage.setItem('role', userData.role);
    localStorage.setItem('username', userData.username);
    localStorage.setItem('id', userData.id);
    
    setUser({ 
        token, 
        role: userData.role, 
        username: userData.username, 
        id: userData.id 
    });
  };


    const register = async (username, email, password) => {
    // IMPORTANT: aici pui endpoint-ul tău de backend
    // Dacă în backend ai /api/auth/register, aici folosești /api/auth/register
    // (pentru că axios global nu are baseURL ca instanța "api")
    const { data } = await axios.post(
      "https://springbootreactpostgres.onrender.com/api/auth/register",
      {
        username: username.trim(),
        email: email?.trim() ? email.trim() : null,
        password,
      }
    );

    // Dacă backend-ul returnează token + role (ca la login), faci autologin:
    if (data?.token) {
      const userData = {
        role: data.role,
        username: data.username || username.trim(),
        id: data.id || "0",
      };
      login(data.token, userData);
    }

    return data;
  };


  const logout = () => {
    localStorage.clear();
    setUser(null);
    window.location = '/login';
  };

  axios.interceptors.request.use(cfg => {
    if (user?.token) cfg.headers.Authorization = `Bearer ${user.token}`;
    return cfg;
  });

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};