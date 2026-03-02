import { useState } from 'react';

import Tooltip from '../../../components/common/Tooltip';
import CreateRoutineModal from './CreateRoutineModal';

import './Routines.css';

export default function Routines({isLoggedIn}) {
    const [showCreateModal, setShowCreateModal] = useState(false);

    function handleAddRoutine() {
        setShowCreateModal(true);
    }

    return (
        <div className='routine-container'>

            <div className="routines-header">
                <h1>Routines</h1>
            </div>

            <div className='routines-subsection-container'>

                <div style={{display: 'flex', gap: '1rem', alignItems: 'center'}}>
                    <h2>Makeup</h2>
                    <Tooltip message="Create a new makeup routine" position="top">
                        <button
                            className="add-routine-button"
                            onClick= {handleAddRoutine}
                        >
                            +
                        </button>
                    </Tooltip>
                </div>

                <h2>Skincare</h2>
                <p>AM</p>
                <p>PM</p>

                <h2>Haircare</h2>

            </div>

            {showCreateModal && <CreateRoutineModal onClose={() => setShowCreateModal(false)} />}
        </div>
    );
}