import "./QuestionCard.css";

import { useEffect, useMemo, useState } from "react";
import {
  editAnswer,
  editQuestion,
  submitAnswer,
  upvoteAnswer,
  upvoteQuestion,
  removeUpvoteAnswer,
  removeUpvoteQuestion,
} from "../api/qaApi";

const isWithinEditWindow = (createdAt) => {
  if (!createdAt) return false;
  const created = new Date(createdAt);
  if (Number.isNaN(created.getTime())) return false;
  const diffMs = Date.now() - created.getTime();
  return diffMs >= 0 && diffMs <= 15 * 60 * 1000;
};

const formatDateTime = (value) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
};

export default function QuestionCard({
  question,
  onRefresh,
  currentUserName,
  isLoggedIn,
  onRequireLogin,
  onToast,
}) {
  if (!question) return null;

  const canEdit = useMemo(
    () => isWithinEditWindow(question.createdAt),
    [question.createdAt]
  );
  const isAuthor = Boolean(
    currentUserName && question.authorName && currentUserName === question.authorName
  );
  const showEdit = isAuthor && canEdit;
  const showAnswer = !isAuthor;
  const askedAt = formatDateTime(question.createdAt);
  const [questionUpvoteCount, setQuestionUpvoteCount] = useState(
    Number(question.upvoteCount ?? 0)
  );
  const [questionHasUpvoted, setQuestionHasUpvoted] = useState(
    Boolean(question.hasUpvoted)
  );
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState(question.text ?? "");
  const [editError, setEditError] = useState("");
  const [isAnswering, setIsAnswering] = useState(false);
  const [answerText, setAnswerText] = useState("");
  const [answerError, setAnswerError] = useState("");
  const [editingAnswerId, setEditingAnswerId] = useState(null);
  const [answerEditText, setAnswerEditText] = useState("");
  const [answerEditError, setAnswerEditError] = useState("");
  const [answerUpvotes, setAnswerUpvotes] = useState({});

  useEffect(() => {
    setEditText(question.text ?? "");
  }, [question.text]);

  useEffect(() => {
    setQuestionUpvoteCount(Number(question.upvoteCount ?? 0));
    setQuestionHasUpvoted(Boolean(question.hasUpvoted));
  }, [question.id, question.upvoteCount, question.hasUpvoted]);

  useEffect(() => {
    const nextUpvotes = {};
    (question.answers ?? []).forEach((answer) => {
      nextUpvotes[answer.id] = {
        count: Number(answer.upvoteCount ?? 0),
        hasUpvoted: Boolean(answer.hasUpvoted),
      };
    });
    setAnswerUpvotes(nextUpvotes);
  }, [question.answers]);

  useEffect(() => {
    if (!editingAnswerId) return;
    const answer = question.answers?.find((item) => item.id === editingAnswerId);
    if (!answer) {
      setEditingAnswerId(null);
      setAnswerEditText("");
      setAnswerEditError("");
    }
  }, [editingAnswerId, question.answers]);

  const handleEditSubmit = async (event) => {
    event.preventDefault();
    const trimmed = editText.trim();
    if (trimmed.length < 10) {
      setEditError("Question must be at least 10 characters.");
      return;
    }

    setEditError("");
    const success = await editQuestion(question.id, trimmed);
    if (success) {
      setIsEditing(false);
      onRefresh?.();
      onToast?.("Question updated.", "success");
    } else {
      setEditError("Unable to update question.");
      onToast?.("Unable to update question.", "error");
    }
  };

  const handleAnswerSubmit = async (event) => {
    event.preventDefault();
    const trimmed = answerText.trim();
    if (trimmed.length < 2) {
      setAnswerError("Answer must be at least 2 characters.");
      return;
    }

    setAnswerError("");
    const success = await submitAnswer(question.id, trimmed);
    if (success) {
      setAnswerText("");
      setIsAnswering(false);
      onRefresh?.();
      onToast?.("Answer submitted.", "success");
    } else {
      setAnswerError("Unable to submit answer.");
      onToast?.("Unable to submit answer.", "error");
    }
  };

  const handleAnswerEditSubmit = async (event) => {
    event.preventDefault();
    const trimmed = answerEditText.trim();
    if (trimmed.length < 2) {
      setAnswerEditError("Answer must be at least 2 characters.");
      return;
    }

    setAnswerEditError("");
    const success = await editAnswer(editingAnswerId, trimmed);
    if (success) {
      setEditingAnswerId(null);
      setAnswerEditText("");
      onRefresh?.();
      onToast?.("Answer updated.", "success");
    } else {
      setAnswerEditError("Unable to update answer.");
      onToast?.("Unable to update answer.", "error");
    }
  };

  const handleQuestionUpvote = async () => {
    if (!isLoggedIn) {
      onRequireLogin?.();
      return;
    }
    if (isAuthor) return;

    const isUpvoted = questionHasUpvoted;
    const success = isUpvoted
      ? await removeUpvoteQuestion(question.id)
      : await upvoteQuestion(question.id);
    if (success) {
      setQuestionHasUpvoted(!isUpvoted);
      setQuestionUpvoteCount((count) => Math.max(0, count + (isUpvoted ? -1 : 1)));
      onToast?.(isUpvoted ? "Question upvote removed." : "Question upvoted.", "success");
    } else {
      onToast?.("Unable to update question upvote.", "error");
    }
  };

  const handleAnswerUpvote = async (answerId, isAnswerAuthor) => {
    if (!isLoggedIn) {
      onRequireLogin?.();
      return;
    }
    if (isAnswerAuthor) return;

    const isUpvoted = Boolean(answerUpvotes[answerId]?.hasUpvoted);
    const success = isUpvoted
      ? await removeUpvoteAnswer(answerId)
      : await upvoteAnswer(answerId);
    if (success) {
      setAnswerUpvotes((prev) => ({
        ...prev,
        [answerId]: {
          count: Math.max(0, (prev[answerId]?.count ?? 0) + (isUpvoted ? -1 : 1)),
          hasUpvoted: !isUpvoted,
        },
      }));
      onToast?.(isUpvoted ? "Answer upvote removed." : "Answer upvoted.", "success");
    } else {
      onToast?.("Unable to update answer upvote.", "error");
    }
  };

  return (
    <div className="question-card">
      <div className="question-card__header">
        <p className="question-text">{question.text}</p>
        <div className="question-header-actions">
          {question.answers?.length ? (
            <span className="question-status">Answered</span>
          ) : null}
          {!isAuthor ? (
            <button
              type="button"
              className="question-action-btn"
              onClick={handleQuestionUpvote}
            >
              {questionHasUpvoted ? "Undo" : "Upvote"}
            </button>
          ) : null}
          <span className="question-upvotes">{questionUpvoteCount} upvotes</span>
        </div>
      </div>
      {showEdit || showAnswer ? (
        <div className="question-actions">
          {showEdit ? (
            <button
              type="button"
              className="question-action-btn"
              onClick={() => {
                setIsEditing((value) => !value);
                setEditError("");
              }}
            >
              {isEditing ? "Cancel edit" : "Edit question"}
            </button>
          ) : null}
          {showAnswer ? (
            <button
              type="button"
              className="question-action-btn"
              onClick={() => {
                if (!isLoggedIn) {
                  onRequireLogin?.();
                  return;
                }
                setIsAnswering((value) => !value);
                setAnswerError("");
              }}
            >
              {isAnswering ? "Cancel answer" : "Answer"}
            </button>
          ) : null}
        </div>
      ) : null}
      <div className="question-meta">
        {isAuthor ? (
          <span>Asked by you</span>
        ) : question.authorName ? (
          <span>Asked by {question.authorName}</span>
        ) : null}
        {askedAt ? <span>{askedAt}</span> : null}
      </div>
      {isEditing ? (
        <form className="question-edit-form" onSubmit={handleEditSubmit}>
          <textarea
            className="question-edit-textarea"
            value={editText}
            onChange={(event) => setEditText(event.target.value)}
            rows={3}
            maxLength={800}
          />
          {editError ? <p className="question-error">{editError}</p> : null}
          <button type="submit" className="question-submit-btn">
            Save changes
          </button>
        </form>
      ) : null}

      {isAnswering ? (
        <form className="question-answer-form" onSubmit={handleAnswerSubmit}>
          <textarea
            className="question-answer-textarea"
            value={answerText}
            onChange={(event) => setAnswerText(event.target.value)}
            rows={3}
            maxLength={800}
          />
          {answerError ? <p className="question-error">{answerError}</p> : null}
          <button type="submit" className="question-submit-btn">
            Submit answer
          </button>
        </form>
      ) : null}

      {question.answers?.length ? (
        <div className="question-answers">
          {question.answers.map((answer) => {
            const isAnswerAuthor = Boolean(
              currentUserName && answer.authorName && currentUserName === answer.authorName
            );
            const isEditingAnswer = editingAnswerId === answer.id;
            const answerUpvoteData = answerUpvotes[answer.id] ?? {
              count: Number(answer.upvoteCount ?? 0),
              hasUpvoted: Boolean(answer.hasUpvoted),
            };
            const answeredAt = formatDateTime(answer.createdAt);

            return (
              <div key={answer.id} className="question-answer">
                <div className="question-answer__header">
                  <p className="answer-text">{answer.text}</p>
                  <div className="question-answer-actions">
                    {isAnswerAuthor ? (
                      <button
                        type="button"
                        className="question-action-btn"
                        onClick={() => {
                          if (!isLoggedIn) {
                            onRequireLogin?.();
                            return;
                          }
                          setEditingAnswerId((value) =>
                            value === answer.id ? null : answer.id
                          );
                          setAnswerEditText(answer.text ?? "");
                          setAnswerEditError("");
                        }}
                      >
                        {isEditingAnswer ? "Cancel edit" : "Edit answer"}
                      </button>
                    ) : null}
                    {!isAnswerAuthor ? (
                      <button
                        type="button"
                        className="question-action-btn"
                        onClick={() => handleAnswerUpvote(answer.id, isAnswerAuthor)}
                      >
                        {answerUpvoteData.hasUpvoted ? "Undo" : "Upvote"}
                      </button>
                    ) : null}
                    <span className="answer-upvotes">{answerUpvoteData.count} upvotes</span>
                  </div>
                </div>
                <div className="answer-meta">
                  {answer.authorName ? (
                    <span>Answered by {answer.authorName}</span>
                  ) : null}
                  {answeredAt ? <span>{answeredAt}</span> : null}
                </div>
                {isEditingAnswer ? (
                  <form className="question-answer-form" onSubmit={handleAnswerEditSubmit}>
                    <textarea
                      className="question-answer-textarea"
                      value={answerEditText}
                      onChange={(event) => setAnswerEditText(event.target.value)}
                      rows={3}
                      maxLength={800}
                    />
                    {answerEditError ? (
                      <p className="question-error">{answerEditError}</p>
                    ) : null}
                    <button type="submit" className="question-submit-btn">
                      Save answer
                    </button>
                  </form>
                ) : null}
              </div>
            );
          })}
        </div>
      ) : null}
    </div>
  );
}
