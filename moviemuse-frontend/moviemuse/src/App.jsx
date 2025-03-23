import './App.css'
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from './components/Navbar';
import Home from './components/Home';
import NewMovie from './components/NewMovie';

function App() {
  

  return (
    <Router>
      <div className="app">
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />}></Route>
          <Route path="/new-movies" element={<NewMovie/>}></Route>
        </Routes>
      </div>
    </Router>
  );
}

export default App
