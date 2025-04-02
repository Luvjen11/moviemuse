import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './components/Home';
import MovieDetail from './components/MovieDetail';
import NewMovie from './components/NewMovie';
import './App.css';
import Navbar from './components/Navbar';

function App() {
  return (
    <Router>
      
      <div className="app">
        <Navbar/>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/add-movie" element={<NewMovie />} />
          <Route path="/movie/:id" element={<MovieDetail />} />
          {/* <Route path="/movie/:id/add-review" element={<ReviewForm />} /> */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
