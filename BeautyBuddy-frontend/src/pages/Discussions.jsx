import { useEffect, useState } from "react";
import { getDiscussions, createDiscussion } from "../api/DiscussionApi";
import DiscussionCard from "../components/discussion/DiscussionCard";
import CreateDiscussionModal from "../components/discussion/CreateDiscussionModal";

export default function Discussions() {
  const [discussions, setDiscussions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);

  const refreshDiscussions = () => {
    setLoading(true);
    getDiscussions().then((data) => {
      setDiscussions(Array.isArray(data) ? data : data.content || []);
      setLoading(false);
    });
  };

  useEffect(() => {
    refreshDiscussions();
  }, []);

  const handleCreateDiscussion = async (title, body) => {
    const success = await createDiscussion(title, body);
    if (success) {
      refreshDiscussions();
      return true;
    }
    return false;
  };

  return (
    <div>
      <h1>Discussions</h1>
      <button onClick={() => setShowModal(true)} style={{marginBottom: 18}}>Create Discussion</button>
      {loading ? (
        <div>Loading...</div>
      ) : discussions.length === 0 ? (
        <div>No discussions yet.</div>
      ) : (
        discussions.map((d) => (
          <DiscussionCard key={d.id} {...d} />
        ))
      )}
      <CreateDiscussionModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        onSubmit={handleCreateDiscussion}
      />
    </div>
  );
}