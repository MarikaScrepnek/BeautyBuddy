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
                <Tooltip message="Create a new routine" position="top">
                    <button
                        className="add-routine-button"
                        onClick= {handleAddRoutine}
                    >
                        +
                    </button>
                </Tooltip>
            </div>
            {showCreateModal && <CreateRoutineModal onClose={() => setShowCreateModal(false)} />}
        </div>
    );
}